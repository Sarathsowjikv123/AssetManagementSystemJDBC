-- Allocate Asset TO User
INSERT INTO AssetAssignmentsSummary (userId, assetId, datetime, operations, status)
SELECT 3, 2, now(), 'ASSIGN', 1
FROM dual
WHERE (SELECT 1 FROM UserAssetMapping WHERE usertypeid = (select usertypeid from user where userid = 3) and assetid = 2) = 1 and 
(select assetCount from asset where assetid = 2) > 0 and EXISTS (select 1 from assetassignmentssummary where userid = 3 and assetid = 2 and operations = 'ASSIGN' and status = 1) = 0;

-- See Requirements of Each User
select t1.userid, group_concat(t1.assetname separator ', ') as assetnames from
(select u.userid, u.usertypeid, uam.assetid, a.assetname from user u join userassetmapping uam on u.usertypeid = uam.usertypeid join asset a on a.assetid = uam.assetid) as t1 where (t1.userid, t1.assetid) not in
(select aa.userid, aa.assetid from assetassignmentssummary aa where aa.operations = "ASSIGN" and aa.status = 1) group by t1.userid;

-- User List
SELECT u.userid, u.username, ut.usertype from user join usertypes ut using (usertypeid);

-- Get user By Id
with cte as (select u.userid, u.username, group_concat(a.assetname separator ', ') as allocated_assets from user u
right join assetassignmentssummary aas on aas.userid = u.userid
left join asset a on a.assetid = aas.assetid
where (aas.operations = "ASSIGN" and aas.status = 1) or (aas.operations = "NEW USER" and aas.status = 0) group by u.userid)
select u.userId, u.userName, ut.userType, coalesce(cte.allocated_assets, "No Assets Allocated") as allocated_assets from cte right join user u on cte.userId = u.userId
join usertypes ut on u.usertypeid = ut.usertypeid where u.userid = 1;

-- Retain Asset From a User
insert into assetassignmentssummary (userid, assetid, datetime, operations, status) values (1, 1, now(), "RETAIN", null);
update assetassignmentssummary set status = false where userid = 1 and assetid = 1 and operations = "ASSIGN";
update retainedassets set reainedAssetCount = retainedAssetCount + 1 where assetid = 1;

-- Display Retained Assets
select r.assetId, a.assetName, r.retainedassetcount from retainedassets r join asset a on r.assetid = a.assetid;

-- Display User Summary
select aas.assetId, a.assetname, aas.datetime, aas.operations,
case
when aas.status = 0 then "IN ACTIVE"
when aas.status = 1 then "ACTIVE"
end as status
from assetassignmentssummary aas join asset a on aas.assetid = a.assetid where aas.userid = 1;

-- Is User Has Assets
select count(*) as noOfRows from (select * from assetassignmentssummary where userid = 2 and operations = "ASSIGN" and status = "1") as t1;

-- Is Asset is assigned to User
select aas.userid, u.userName from assetassignmentssummary aas join user u on aas.userid = u.userid where aas.assetid = 2 and aas.operations = "ASSIGN" and aas.status = 1;

-- Check Role and Retain Asset
select aas.assetid from assetassignmentssummary aas where aas.userid = 1 and aas.operations = "ASSIGN" and aas.status = true
and aas.assetid not in (select assetId from userassetmapping where usertypeid = 2);

-- Check For Valid Request
SELECT EXISTS (SELECT 1 FROM UserAssetMapping WHERE assetId = 4 and userTypeId = (SELECT userTypeId FROM User WHERE userId = 4));
SELECT EXISTS (SELECT 1 FROM assetassignmentssummary WHERE assetId = 7 and userId = 4 and operations = "ASSIGN" and status = 1);

-- Display all Requests
select rt.request_id, rt.user_id, u.userName, rt.asset_id, a.assetName, rt.operations, rt.given_date_time, rt.completed_date_time, rt.status 
from request_table rt join user u on u.userId = rt.user_id join asset a on rt.asset_id = a.assetid;

-- Display Incompleted Requests
select rt.request_id, rt.user_id, u.userName, rt.asset_id, a.assetName, rt.operations, rt.given_date_time, rt.completed_date_time, rt.status 
from request_table rt join user u on u.userId = rt.user_id join asset a on rt.asset_id = a.assetid where status = 0;

-- Display Completed Requests
select rt.request_id, rt.user_id, u.userName, rt.asset_id, a.assetName, rt.operations, rt.given_date_time, rt.completed_date_time, rt.status 
from request_table rt join user u on u.userId = rt.user_id join asset a on rt.asset_id = a.assetid where status = 1;

-- Allocate or Retain asset Based on Request
update request_table set status = 1 where user_id = 3 and asset_id = 6 and operations = "ASSIGN" and status = 0;
update request_table set status = 1 where user_id = 3 and asset_id = 6 and operations = "RETAIN" and status = 0;

-- Display Assets on Raise request
SELECT uam.assetid, a.assetname from user u join userassetmapping uam on u.usertypeid = uam.usertypeid join asset a on uam.assetid = a.assetid where userid = 6;

-- Display all time history
select aas.userid, u.username, aas.assetid, a.assetname, aas.datetime, aas.operations, 
case
when aas.status = 1 then "ACTIVE"
when aas.status = 0 then "INACTIVE"
end as status
from assetassignmentssummary aas
join allusers u on aas.userid = u.userid join allassets a on aas.assetid = a.assetid;

-- List of assets allocated to each user
select u.userid, group_concat(a.assetname separator ', ') from user u left join assetassignmentssummary aas using (userid)
left join asset a using (assetid)
where operations = "ASSIGN" and status = 1 group by u.userid;