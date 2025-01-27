import java.sql.*;
public class UserManagement {
    int rowsAffected;
    //Add New User
    public void addUser(String userName, User.UserType userType,String newPassword, AssetManagement assetManagement, UserManagement userManagement) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String query = "INSERT INTO allusers (usertypeid, username) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userType.value);
            ps.setString(2, userName);
            int generatedId = 0;
            rowsAffected = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rowsAffected > 0) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
                System.out.println();
                System.out.println("User added successfully !!!");
                System.out.println();

                query = "INSERT INTO user (userid, usertypeid, username, password) VALUES (? , ?, ?, ?)";
                PreparedStatement ps1 = conn.prepareStatement(query);
                ps1.setInt(1, generatedId);
                ps1.setInt(2, userType.value);
                ps1.setString(3, userName);
                ps1.setString(4, newPassword);
                rowsAffected = ps1.executeUpdate();

            } else {
                System.out.println();
                System.out.println("User not added");
                System.out.println();
            }
            conn.commit();
            conn.setAutoCommit(true);
        }catch(SQLException e) {
            conn.rollback();
            System.out.println("SQL Rollback !!!");
            DBConnection.closeConnection();
        }
        getUserList();
    }

    //Allocate Asset To User
    public void allocateAsset(int userId, int assetId, AssetManagement assetManagement, UserManagement userManagement) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            String query = "INSERT INTO AssetAssignmentsSummary (userId, assetId, datetime, operations, status)" +
                    " SELECT ?, ?, now(), 'ASSIGN', 1" +
                    " FROM dual" +
                    " WHERE (SELECT 1 FROM UserAssetMapping WHERE usertypeid = (select usertypeid from user where userid = ?) and assetid = ?) = 1 and" +
                    " (select assetCount from asset where assetid = ?) > 0 and EXISTS (select 1 from assetassignmentssummary where userid = ? and assetid = ? and operations = 'ASSIGN' and status = 1) = 0;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, assetId);
            ps.setInt(3, userId);
            ps.setInt(4, assetId);
            ps.setInt(5, assetId);
            ps.setInt(6, userId);
            ps.setInt(7, assetId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println();
                System.out.println("Asset Allocated successfully !!!");
                System.out.println();
                query = "update asset set assetcount = assetcount - 1 where assetid = ?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, assetId);
                rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println();
                    System.out.println("Asset Count updated successfully !!!");
                    System.out.println();
                    //assetManagement.getAssetList();
                } else {
                    System.out.println();
                    System.out.println("Asset Count not updated successfully !!!");
                    System.out.println();
                }
                query = "update request_table set status = 1, completed_date_time = now() where user_id = ? and asset_id = ? and operations = \"ASSIGN\" and status = 0";
                ps = conn.prepareStatement(query);
                ps.setInt(1, userId);
                ps.setInt(2, assetId);
                rowsAffected = ps.executeUpdate();
                if (rowsAffected < 1) {
                    System.out.println();
                    System.out.println("Asset Allocated Without Request From The User !!!");
                    System.out.println();
                }
            } else {
                System.out.println();
                System.out.println("Asset Not Allocated !!!");
                System.out.println();
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            System.out.println();
            System.out.println("Allocation Failed and Rollback !!!");
            System.out.println(e);
            System.out.println();
        }
        conn.setAutoCommit(true);
        assetManagement.getAssetList();
        getUserList();
        DBConnection.closeConnection();
    }

    //See Requirements
    public void getRequirements() throws SQLException {
        System.out.println();
        System.out.println("***** SEE ALL POSSIBLE ASSIGNMENTS *****");
        String query = "select t1.userid, group_concat(t1.assetname separator ', ') as assetnames from" +
                " (select u.userid, u.usertypeid, uam.assetid, a.assetname from user u join userassetmapping uam on u.usertypeid = uam.usertypeid join asset a on uam.assetid = a.assetid) as t1 where (t1.userid, t1.assetid) not in" +
                " (select aa.userid, aa.assetid from assetassignmentssummary aa where aa.operations = 'ASSIGN' and aa.status = 1) group by t1.userid;";
        ResultSet rs = DBConnection.executeQuery(query);
        while(rs.next()){
            int userid = rs.getInt(1);
            String assetnames = rs.getString(2);
            System.out.println("User Id : "+userid + " \t --> \t Required Assets : " + assetnames);
        }
        DBConnection.closeConnection();
        System.out.println();
    }

    //User List
    public void getUserList() throws SQLException {
        System.out.println();
        System.out.println("***** USER LIST *****");
        String query = "SELECT u.userid, u.username, ut.usertype from user u\n" +
                "join usertypes ut using (usertypeid)";
        ResultSet rs = DBConnection.executeQuery(query);
        String header = "%-10s %-20s %-15s\n";
        String row = "%-10d %-20s %-15s\n";
        System.out.println("*".repeat(45));
        System.out.printf(header,"User-ID", "Username", "UserType");
        System.out.println("*".repeat(45));
        while(rs.next()){
            int userId = rs.getInt(1);
            String userName = rs.getString(2);
            String userType = rs.getString(3);
            System.out.printf(row,userId,userName,userType);
        }
        DBConnection.closeConnection();
        System.out.println("*".repeat(45));
    }

    //Getting a User By Id
    public void getUserByID(int userUd) throws SQLException{
        System.out.println("************************");
        System.out.println("      USER SUMMARY      ");
        System.out.println("************************");
        Connection conn = DBConnection.getConnection();
        String query = "with cte as (select u.userid, u.username, group_concat(a.assetname separator ', ') as allocated_assets from user u " +
                "right join assetassignmentssummary aas on aas.userid = u.userid " +
                "left join asset a on a.assetid = aas.assetid " +
                "where (aas.operations = \"ASSIGN\" and aas.status = 1) or (aas.operations = \"NEW USER\" and aas.status = 0) group by u.userid) " +
                "select u.userId, u.userName, ut.userType, coalesce(cte.allocated_assets, \"No Assets Allocated\") as allocated_assets from cte right join user u on cte.userId = u.userId " +
                "join usertypes ut on u.usertypeid = ut.usertypeid where u.userid = ?;";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1,userUd);
        ResultSet rs = ps.executeQuery();
        if(!rs.isBeforeFirst()){
            System.out.println(" No Assets Allocated OR Invalid User Id !!!");
        }else {
            while (rs.next()) {
                int userId = rs.getInt(1);
                String userName = rs.getString(2);
                String userType = rs.getString(3);
                String allocatedAssets = rs.getString(4);
                System.out.println("USER ID : " + userId);
                System.out.println("USER NAME : " + userName);
                System.out.println("USER TYPE : " + userType);
                System.out.println("ALLOCATED ASSETS : " + allocatedAssets);
            }
        }
        DBConnection.closeConnection();
        System.out.println();
    }

    //Display User Summary
    public void displayUserSummary(int userId) throws SQLException{
        System.out.println();
        System.out.println("*******************************************");
        System.out.println("      USER ALLOCATIONS AND RETENTIONS      ");
        System.out.println("*******************************************");
        System.out.println();
        Connection conn = DBConnection.getConnection();
        String query = "select aas.assetId, a.assetname, aas.datetime, aas.operations, " +
                "case " +
                "when aas.status = 0 then \"IN ACTIVE\" " +
                "when aas.status = 1 then \"ACTIVE\" " +
                "end as status " +
                "from assetassignmentssummary aas join allassets a on aas.assetid = a.assetid where aas.userid = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1,userId);
        ResultSet rs = ps.executeQuery();
        System.out.println("ASSET ID \t" + "ASSET NAME \t\t\t" + "DATE & TIME\t\t\t" + "OPERATION\t" + "ASSET STATUS");
        while(rs.next()){
            int assetId = rs.getInt(1);
            String assetName = rs.getString(2);
            String datetime = rs.getString(3);
            String operations = rs.getString(4);
            String status = rs.getString(5);
            System.out.println("\t" + assetId +"\t\t"+ assetName +"\t\t\t"+ datetime +"\t\t "+ operations +"\t\t"+ status);
        }
        DBConnection.closeConnection();
        System.out.println();
    }

    //Remove a User
    public void removeUser(int userId) throws SQLException{
        System.out.println();
        System.out.println("***** REMOVE AN USER *****");
        System.out.println();
        if(!isUserHasAsset(userId)){
            Connection conn = DBConnection.getConnection();
            String query = "delete from user where userid = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,userId);
            if(ps.executeUpdate() > 0){
                System.out.println();
                System.out.println("User " + userId + " has been removed !!!");
                System.out.println();
            }else{
                System.out.println();
                System.out.println("User " + userId + " has not been removed !!!");
                System.out.println();
            }
            DBConnection.closeConnection();
        }else{
            System.out.println();
            System.out.println("User " + userId + " HAS Active Assets Allocated !!!");
            System.out.println("You need to remove all the assets allocated to this User to Remove this User !!!");
            System.out.println();
        }
        getUserList();
    }

    //Check Whether an asset is allocated to the User
    public boolean isUserHasAsset(int userId) throws SQLException{
        Connection conn = DBConnection.getConnection();
        String query = "select count(*) as noOfRows from (select * from assetassignmentssummary where userid = ? and operations = \"ASSIGN\" and status = \"1\") as t1";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1,userId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            int noOfRows = rs.getInt(1);
            if(noOfRows > 0){
                DBConnection.closeConnection();
                return true;
            }
        }
        DBConnection.closeConnection();
        return false;
    }

    public void updateUserInfoAndRoles(int userId, String userName, User.UserType userType, AssetManagement assetManagement, UserManagement userManagement) throws SQLException{
        System.out.println("***** UPDATE USER INFO & ROLE *****");
        Connection conn = DBConnection.getConnection();
        String query = "update user set username = ?, usertypeid = ? where userid = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1,userName);
        ps.setInt(2,userType.value);
        ps.setInt(3,userId);
        if(ps.executeUpdate() > 0){
            System.out.println();
            System.out.println("User " + userId + " has been updated !!!");
            System.out.println();
            checkRoleAndRetainAsset(userId, userType.value, assetManagement, userManagement);
            getUserList();
        }else{
            System.out.println();
            System.out.println("User Not Found !!!");
            System.out.println();
        }
        getUserList();
        DBConnection.closeConnection();
    }

    //Check User Role and Retain Asset
    public void checkRoleAndRetainAsset(int userId, int userTypeId, AssetManagement assetManagement, UserManagement userManagement) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "select aas.assetid from assetassignmentssummary aas where aas.userid = ? and aas.operations = \"ASSIGN\" and aas.status = true\n" +
                "and aas.assetid not in (select assetId from userassetmapping where usertypeid = ?);";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1,userId);
        ps.setInt(2,userTypeId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            int assetId = rs.getInt(1);
            assetManagement.retainAsset(userId, assetId, userManagement);
            System.out.println();
            System.out.println("Role Checked and UnMapped Asset Retained !!!");
            System.out.println();
        }
        //DBConnection.closeConnection();
    }

    //RAISE A REQUEST
    public void raiseRequest(int userId, int assetId, String operation) throws SQLException{
        Connection conn1 = DBConnection.getConnection();
        String query1 = "select user_id, asset_id, status from request_table where user_id = ? and asset_id = ? and status = 0 and completed_date_time is null;";
        PreparedStatement ps1 = conn1.prepareStatement(query1);
        ps1.setInt(1,userId);
        ps1.setInt(2,assetId);
        ResultSet rs = ps1.executeQuery();
        if(rs.next()){
            System.out.println("Request Already raised !!!");
            DBConnection.closeConnection();
            return;
        }
        if(checkForValidRequest(userId, assetId, operation)) {
            Connection conn = DBConnection.getConnection();
            String query = "INSERT INTO request_table (user_id, asset_id, operations, given_date_time, completed_date_time, status) VALUES (?, ?, ?, NOW(), NULL, 0);";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, assetId);
            ps.setString(3, operation);
            if (ps.executeUpdate() > 0) {
                System.out.println("Request Raised Successfully !!!");
            } else {
                System.out.println("Error Raising Request !!!");
            }
        }else{
            System.out.println();
            System.out.println("Not A Valid Request !!!");
            System.out.println();
        }
        DBConnection.closeConnection();
    }

    //CHECKING FOR A VALID REQUEST
    public boolean checkForValidRequest(int userId, int assetId, String operation) throws SQLException{
        Connection conn = DBConnection.getConnection();
        if(operation.equals("ASSIGN")){
            //Checking for asset availability based on role
            String query = "SELECT EXISTS (SELECT 1 FROM UserAssetMapping WHERE assetId = ? and userTypeId = (SELECT userTypeId FROM User WHERE userId = ?))";
            //Checking whether the asset is already assigned or not
            String query2 = "SELECT EXISTS (SELECT 1 FROM assetassignmentssummary WHERE assetId = ? and userId = ? and operations = \"ASSIGN\" and status = 1)";
            PreparedStatement ps = conn.prepareStatement(query);
            PreparedStatement ps2 = conn.prepareStatement(query2);
            ps.setInt(1,assetId);
            ps.setInt(2,userId);
            ps2.setInt(1,assetId);
            ps2.setInt(2,userId);
            ResultSet rs2 = ps2.executeQuery();
            ResultSet rs = ps.executeQuery();
            if(rs.next() && rs2.next()){
                if(rs.getInt(1) == 1 & rs2.getInt(1) == 0){
                    DBConnection.closeConnection();
                    return true;
                }
            }
        } else if (operation.equals("RETAIN")) {
            //Checking whether thw asset is assigned or not
            String query = "SELECT EXISTS (SELECT 1 FROM assetassignmentssummary WHERE assetId = ? and userId = ? and operations = \"ASSIGN\" and status = 1)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,assetId);
            ps.setInt(2,userId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                if(rs.getInt(1) == 1){
                    DBConnection.closeConnection();
                    return true;
                }
            }
        }
        DBConnection.closeConnection();
        return false;
    }

    //Display All Requests
    public void displayAllRequests() throws SQLException{
        System.out.println();
        System.out.println("***** ALL REQUESTS *****");
        System.out.println();
        String query = "select rt.request_id, rt.user_id, u.userName, rt.asset_id, a.assetName, rt.operations, rt.given_date_time, rt.completed_date_time, rt.status \n" +
                "from request_table rt join allusers u on u.userId = rt.user_id join allassets a on rt.asset_id = a.assetid;";
        ResultSet rs = DBConnection.executeQuery(query);
        String header = "%-15s %-15s %-15s %-15s %-15s %-15s %-25s %-25s %-20s\n";
        System.out.println("*".repeat(155));
        System.out.printf(header, "Request-ID", "User-ID", "Username", "Asset-ID", "Asset-Name", "Operation", "Given Date Time", "Completed Date Time", "Status");
        System.out.println("*".repeat(155));
        while(rs.next()){
            int requestId = rs.getInt(1);
            int userId = rs.getInt(2);
            String userName = rs.getString(3);
            int assetId = rs.getInt(4);
            String assetName = rs.getString(5);
            String operation = rs.getString(6);
            String givenDateTime = rs.getString(7);
            String completedDateTime = rs.getString(8);
            String status = rs.getString(9);

            String row = "%-15d %-15d %-15s %-15d %-15s %-15s %-25s %-25s %-20s\n";
            System.out.printf(row, requestId, userId, userName, assetId, assetName, operation, givenDateTime, completedDateTime, status);
            //System.out.println(requestId+"\t User Id : "+userId+"\t"+userName+"\t Asset Id : "+assetId+"\t"+assetName+"\t"+operation+"\t Requested Time : "+givenDateTime+"\t Completed Time : "+completedDateTime+"\t Status : "+status);
        }
        System.out.println("*".repeat(155));
        System.out.println();
        DBConnection.closeConnection();
    }

    //Display Incompleted Requests
    public void displayIncompletedRequests() throws SQLException{
        System.out.println();
        System.out.println("***** INCOMPLETED REQUESTS *****");
        System.out.println();
        Connection conn = DBConnection.getConnection();
        String query = "select rt.request_id, rt.user_id, u.userName, rt.asset_id, a.assetName, rt.operations, rt.given_date_time, rt.completed_date_time, rt.status \n" +
                "from request_table rt join allusers u on u.userId = rt.user_id join allassets a on rt.asset_id = a.assetid where status = 0";
        ResultSet rs = DBConnection.executeQuery(query);
        String header = "%-15s %-15s %-15s %-15s %-15s %-15s %-25s %-25s %-20s\n";
        System.out.println("*".repeat(155));
        System.out.printf(header, "Request-ID", "User-ID", "Username", "Asset-ID", "Asset-Name", "Operation", "Given Date Time", "Completed Date Time", "Status");
        System.out.println("*".repeat(155));
        while(rs.next()){
            int requestId = rs.getInt(1);
            int userId = rs.getInt(2);
            String userName = rs.getString(3);
            int assetId = rs.getInt(4);
            String assetName = rs.getString(5);
            String operation = rs.getString(6);
            String givenDateTime = rs.getString(7);
            String completedDateTime = rs.getString(8);
            String status = rs.getString(9);
            String row = "%-15d %-15d %-15s %-15d %-15s %-15s %-25s %-25s %-20s\n";
            System.out.printf(row, requestId, userId, userName, assetId, assetName, operation, givenDateTime, completedDateTime, status);
            //System.out.println(requestId+"\t User Id : "+userId+"\t"+userName+"\t Asset Id : "+assetId+"\t"+assetName+"\t"+operation+"\t Requested Time : "+givenDateTime+"\t Completed Time : "+completedDateTime+"\t Status : "+status);
        }
        System.out.println("*".repeat(155));
        System.out.println();
    }

    //Display Completed Requests
    public void displayCompletedRequests() throws SQLException{
        System.out.println();
        System.out.println("***** COMPLETED REQUESTS *****");
        System.out.println();
        Connection conn = DBConnection.getConnection();
        String query = "select rt.request_id, rt.user_id, u.userName, rt.asset_id, a.assetName, rt.operations, rt.given_date_time, rt.completed_date_time, rt.status \n" +
                "from request_table rt join allusers u on u.userId = rt.user_id join allassets a on rt.asset_id = a.assetid where status = 1";
        ResultSet rs = DBConnection.executeQuery(query);
        String header = "%-15s %-15s %-15s %-15s %-15s %-15s %-25s %-25s %-20s\n";
        System.out.println("*".repeat(155));
        System.out.printf(header, "Request-ID", "User-ID", "Username", "Asset-ID", "Asset-Name", "Operation", "Given Date Time", "Completed Date Time", "Status");
        System.out.println("*".repeat(155));
        while(rs.next()){
            int requestId = rs.getInt(1);
            int userId = rs.getInt(2);
            String userName = rs.getString(3);
            int assetId = rs.getInt(4);
            String assetName = rs.getString(5);
            String operation = rs.getString(6);
            String givenDateTime = rs.getString(7);
            String completedDateTime = rs.getString(8);
            String status = rs.getString(9);
            String row = "%-15d %-15d %-15s %-15d %-15s %-15s %-25s %-25s %-20s\n";
            System.out.printf(row, requestId, userId, userName, assetId, assetName, operation, givenDateTime, completedDateTime, status);
            //System.out.println(requestId+"\t User Id : "+userId+"\t"+userName+"\t Asset Id : "+assetId+"\t"+assetName+"\t"+operation+"\t Requested Time : "+givenDateTime+"\t Completed Time : "+completedDateTime+"\t Status : "+status);
        }
        System.out.println("*".repeat(155));
        System.out.println();
    }

    //Check For an Active User
    public boolean isUserAvailable(int userId, String password) throws SQLException{
        Connection conn = DBConnection.getConnection();
        String query = "select 1 WHERE EXISTS(select userid from user where userid = ? and password = ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return true;
        }
        return false;
    }

    //List of assets allocated to users
    public void assetsAllocatedToUsers() throws SQLException{
        Connection conn = DBConnection.getConnection();
        String query = "select u.userid, group_concat(a.assetname separator ', ') as assets from user u left join assetassignmentssummary aas using (userid)\n" +
                "left join asset a using (assetid)\n" +
                "where operations = \"ASSIGN\" and status = 1 \n" +
                "group by u.userid;";
        ResultSet rs = DBConnection.executeQuery(query);
        String header = "%-10s %-10s\n";
        System.out.println("*".repeat(50));
        System.out.printf(header, "User-ID", "ALLOCATED ASSETS");
        System.out.println("*".repeat(50));
        String row = "%-10d %-10s\n";
        while(rs.next()){
            int userId = rs.getInt(1);
            String assetName = rs.getString(2);
            System.out.printf(row, userId, assetName);
        }
        System.out.println("*".repeat(50));
        System.out.println();
    }

}
