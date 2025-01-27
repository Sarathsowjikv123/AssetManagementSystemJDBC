import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class AssetManagement {
    public List<Asset> assetList = new ArrayList<>();
    public int rowsAffected = 0;

    //Add New Asset
    public void addAsset(String assetName, Asset.AssetType assetType, int assetCount) throws SQLException{
        Connection conn = DBConnection.getConnection();
        String query = "INSERT INTO allassets (assetName, assetType) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, assetName);
        ps.setString(2, assetType.toString());
        int generatedId = 0;
        rowsAffected = ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if(rowsAffected > 0){
            if(rs.next()){
                generatedId = rs.getInt(1);
            }
            System.out.println();
            System.out.println("Asset added To All Assets successfully !!!");
            System.out.println();
            try {
                conn.setAutoCommit(false);

                query = "insert into asset (assetId, assetname,assetType, assetCount) values (?, ?, ?, ?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, generatedId);
                ps.setString(2, assetName);
                ps.setString(3, assetType.toString());
                ps.setInt(4, assetCount);
                if(ps.executeUpdate() > 0){
                    System.out.println("Asset added Assets successfully !!!");
                }

                //Inserting into Retained Asset Table
                query = "insert into retainedassets (assetId, retainedassetcount) values (?, ?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, generatedId);
                ps.setInt(2, 0);
                ps.executeUpdate();

                conn.commit();

            }catch (SQLException e){
                conn.rollback();
            }
            conn.setAutoCommit(true);
            DBConnection.closeConnection();
            updateUserAndAssetMapping(generatedId, new Scanner(System.in));
        }else{
            DBConnection.closeConnection();
            System.out.println();
            System.out.println("Error adding asset !!!");
            System.out.println();
        }
        getAssetList();
        displayAllAssets();
    }

    //Update User and Asset Mapping
    public void updateUserAndAssetMapping(int assetId, Scanner sc) throws SQLException{
        System.out.println("***** MAPPING ASSET AND USER ******");
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = null;
        for(User.UserType userType : User.UserType.values()){
            System.out.print("For "+userType+"(1 Yes / 2 No) : ");
            int choice = sc.nextInt();
            switch(choice){
                case 1:
                    String query = "insert into userAssetMapping (assetId, userTypeId) values (?,?)";
                    ps = conn.prepareStatement(query);
                    ps.setInt(1, assetId);
                    ps.setInt(2, userType.ordinal()+1);
                    rowsAffected = ps.executeUpdate();
                    break;
                case 2:
                    break;
                default:
                    System.out.println();
                    System.out.println("Invalid choice !!!");
                    System.out.println();
                    break;
            }
        }
        if(rowsAffected > 0){
            System.out.println();
            System.out.println("User and Asset Mapped successfully !!!");
            System.out.println();
        }else{
            System.out.println();
            System.out.println("Error adding user !!!");
            System.out.println();
        }
        DBConnection.closeConnection();
    }

    //loadDataFromDB
    public void getAssetList() throws SQLException{
        assetList.clear();
        try {
            String query = "SELECT * FROM Asset";
            ResultSet rs = DBConnection.executeQuery(query);
            while (rs.next()) {
                int assetId = rs.getInt(1);
                String assetName = rs.getString(2);
                String assetType = rs.getString(3);
                assetType = assetType.toUpperCase();
                int assetCount = rs.getInt(4);
                Asset asset = new Asset(assetId, assetName, Asset.AssetType.valueOf(assetType), assetCount);
                assetList.add(asset);
            }
            DBConnection.closeConnection();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Display All Assets
    public void displayAllAssets() throws SQLException {
        System.out.println();
        System.out.println("***** ASSET LIST *****");
        System.out.println();
        String header = "%-10s %-25s %-15s %-10s\n";
        String row = "%-10d %-25s %-15s %-10s\n";
        System.out.println("*".repeat(70));
        System.out.printf(header,"Asset-ID", "AssetName", "AssetType", "Available Count");
        System.out.println("*".repeat(70));
        if(assetList.isEmpty()) {
            getAssetList();
        }
        for (Asset asset : assetList) {
            System.out.printf(row,asset.getAssetId(),asset.getAssetName(),asset.getAssetType().toString(),asset.getAssetCount());
        }
        System.out.println("*".repeat(70));
        displayUserRolesAndAssets();
        //DBConnection.closeConnection();
    }

    //User Roles & Asset Mapping
    public void displayUserRolesAndAssets() {
        System.out.println();
        System.out.println("***** USER ROLES & ASSETS *****");
        System.out.println();
        try{
            String header = "%-20s %-15s %-15s\n";
            System.out.println("*".repeat(70));
            System.out.printf(header,"User-Type-ID", "User Type", "Assets");
            System.out.println("*".repeat(70));
            String row = "%-20d %-15s %-15s\n";
            String query = "select aa.usertypeid, ut.usertype, group_concat(a.assetname separator ', ') as assetnames from userAssetMapping aa natural join asset a natural join usertypes ut group by aa.usertypeid;";
            ResultSet rs = DBConnection.executeQuery(query);
            while (rs.next()) {
                int userTypeId = rs.getInt(1);
                String userType = rs.getString(2);
                String assetNames = rs.getString(3);
                System.out.printf(row, userTypeId, userType, assetNames);
            }
            System.out.println("*".repeat(70));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Update Asset Inventory
    public void updateAssetInventory(int assetId, String assetName, Asset.AssetType assetType, int assetCount) throws SQLException{
        System.out.println();
        System.out.println("***** UPDATE ASSET INFORMATION IN INVENTORY*****");
        Connection conn = DBConnection.getConnection();
        String query = "update asset set assetName = ?, assetType = ?, assetCount = ? where assetId = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, assetName);
        ps.setString(2, assetType.toString());
        ps.setInt(3, assetCount);
        ps.setInt(4, assetId);
        int rowsAffected = ps.executeUpdate();
        if(rowsAffected > 0){
            System.out.println();
            System.out.println("Asset Updated successfully !!!");
            System.out.println();
        }else{
            System.out.println();
            System.out.println("Error updating asset !!!");
            System.out.println();
        }
        getAssetList();
        displayAllAssets();
        DBConnection.closeConnection();
    }

    //Get Asset By Id
    public Asset getAsset(int assetId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM asset WHERE assetId = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, assetId);
        ResultSet rs = ps.executeQuery();
        String assetName = "";
        String assetType = "";
        int assetCount = 0;
        while(rs.next()){
            assetName = rs.getString(2);
            assetType = rs.getString(3);
            assetCount = rs.getInt(4);
        }
        return new Asset(assetId, assetName, Asset.AssetType.valueOf(assetType), assetCount);
    }

    //Retain an asset from user
    public void retainAsset(int userId, int assetId, UserManagement userManagement){
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            String insertIntoSummary = "insert into assetassignmentssummary (userid, assetid, datetime, operations, status) values (?, ?, now(), \"RETAIN\", 0)";
            PreparedStatement ps = conn.prepareStatement(insertIntoSummary);
            ps.setInt(1, userId);
            ps.setInt(2, assetId);
            if(ps.executeUpdate() < 1){
                System.out.println();
                System.out.println("Error retaining asset !!!");
            }

            String updateSummary = "update assetassignmentssummary set status = false where userid = ? and assetid = ? and operations = \"ASSIGN\"";
            ps = conn.prepareStatement(updateSummary);
            ps.setInt(1, userId);
            ps.setInt(2, assetId);
            if(ps.executeUpdate() < 1){
                System.out.println();
                System.out.println("Error retaining asset !!!");
                System.out.println();
            }

            String insertIntoRetainedAssetd = "update retainedassets set retainedAssetCount = retainedAssetCount + 1 where assetid = ?";
            ps = conn.prepareStatement(insertIntoRetainedAssetd);
            ps.setInt(1, assetId);
            if(ps.executeUpdate() < 1){
                System.out.println();
                System.out.println("Error retaining asset !!!");
            }

            String updateRequest = "update request_table set status = 1, completed_date_time = now() where user_id = ? and asset_id = ? and operations = \"RETAIN\" and status = 0";
            ps = conn.prepareStatement(updateRequest);
            ps.setInt(1, userId);
            ps.setInt(2, assetId);


            conn.commit();
            System.out.println("Asset Retained successfully !!!");
            System.out.println();
            System.out.println();


        }catch (SQLException e){
            if(conn != null){
                try {
                    conn.rollback();
                    System.out.println();
                    System.out.println("Transaction rolled back !!!");
                    System.out.println();
                }catch(SQLException e1){
                    System.out.println(e1);
                }
            }
        }finally {
            if(conn != null){
                try {
                    conn.setAutoCommit(true);
                    //DBConnection.closeConnection();
                } catch (SQLException e) {
                    System.out.println(e);
                }
            }
        }
    }

    //Displaying the Retained Assets
    public void displayRetainedAssets() throws SQLException {
        System.out.println();
        System.out.println("***** RETAINED ASSETS *****");
        System.out.println();
        String query = "select r.assetId, a.assetName, r.retainedassetcount from retainedassets r join asset a on r.assetid = a.assetid";
        ResultSet rs = DBConnection.executeQuery(query);
        while (rs.next()) {
            int assetId = rs.getInt(1);
            String assetName = rs.getString(2);
            int assetCount = rs.getInt(3);
            System.out.println("Asset Id : " + assetId + "\t AssetName : " + assetName + "\t\t AssetCount : " + assetCount + "\n");
        }
        DBConnection.closeConnection();
        System.out.println();
    }

    //Retain all assets of a particular User
    public void retainAllAssetsOfUser(int userId, UserManagement userManagement) throws SQLException{
        System.out.println();
        System.out.println("***** Retain All Assets *****");
        System.out.println();
        Connection conn = DBConnection.getConnection();
        String query = "select assetid from assetassignmentssummary where userid = ? and operations = \"ASSIGN\" and status = \"1\"";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1,userId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int assetId = rs.getInt(1);
            try {
                retainAsset(userId, assetId, userManagement);
            } catch (Exception e) {
                System.out.println();
                System.out.println("Cannot Retain All Assets !!!");
                System.out.println();
                break;
            }
        }
        userManagement.getUserList();
        DBConnection.closeConnection();
    }

    //Remove an Asset
    public void removeAnAsset(int assetId) throws SQLException {
        if(!isAssetAllocated(assetId)){
            Connection conn = DBConnection.getConnection();
            String query = "delete from asset where assetid = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, assetId);
            if(ps.executeUpdate() > 0){
                System.out.println();
                System.out.println("Asset Removed successfully !!!");
                System.out.println();
                query = "DELETE FROM userassetmapping WHERE assetid = ?";
                ps = conn.prepareStatement(query);
                ps.setInt(1, assetId);
                ps.executeUpdate();
                DBConnection.closeConnection();
                getAssetList();
            }else{
                System.out.println();
                System.out.println("Asset Not Removed !!!");
                System.out.println();
                DBConnection.closeConnection();
            }
        }else{
            System.out.println();
            System.out.println("This Asset is Allocated to Several Users !!!");
            System.out.println("Retain Those Assets From the Users to Remove it From The Inventory !!!");
            System.out.println();
            displayAssetsAndUsers(assetId);
        }
        getAssetList();
        //displayAllAssets();
    }

    // Check whether an asset is allocated to an user or not
    public boolean isAssetAllocated(int assetId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "select count(*) as noOfRows from (select aas.userid from assetassignmentssummary aas where aas.assetid = ? and aas.operations = \"ASSIGN\" and aas.status = 1) as t1;";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, assetId);
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

    //Display Assets and Users
    public void displayAssetsAndUsers(int assetId) throws SQLException {
        System.out.println();
        System.out.println("***** DISPLAY ASSETS AND USERS *****");
        System.out.println("ASSET ID : " + assetId + " IS ALLOCATED TO ");
        System.out.println();
        Connection conn = DBConnection.getConnection();
        String query = "select aas.userid, u.userName from assetassignmentssummary aas join user u on aas.userid = u.userid where aas.assetid = ? and aas.operations = \"ASSIGN\" and aas.status = 1;";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, assetId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int userId = rs.getInt(1);
            String userName = rs.getString(2);
            System.out.println("User Id : " + userId + "\t UserName : " + userName + "\n");
        }
        DBConnection.closeConnection();
    }

    //Update Asset Mapping
    public void updateAssetMapping(int assetId, Scanner sc, UserManagement userManagement, AssetManagement assetManagement) throws SQLException{
        Connection conn = DBConnection.getConnection();
        int choice;
        for(User.UserType userType : User.UserType.values()) {
            System.out.print("For "+userType+"(Yes 1 / No 2) : ");
            choice = sc.nextInt();
            sc.nextLine();
            switch(choice) {
                case 1:
                    String query = "INSERT IGNORE INTO userassetmapping (assetid, usertypeid)\n" +
                            "VALUES (?, ?)";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setInt(1, assetId);
                    ps.setInt(2, userType.ordinal()+1);
                    if (ps.executeUpdate() > 0) {
                        System.out.println();
                        System.out.println("User And Asset Mapped Successfully !!!");
                        System.out.println();

                        //Checking for Unmapped Assets
                        query = "select userid from user where usertypeid = ?";
                        ps = conn.prepareStatement(query);
                        ps.setInt(1, userType.value);
                        ResultSet rs = ps.executeQuery();
                        while(rs.next()){
                            int userId = rs.getInt(1);
                            userManagement.checkRoleAndRetainAsset(userId, userType.ordinal()+1, assetManagement, userManagement);
                        }

                    }else{
                        System.out.println();
                        System.out.println("Asset Already Mapped !!!");
                        System.out.println();
                    }
                    break;
                case 2:
                    query  = "DELETE FROM userassetmapping WHERE assetid = ? and usertypeid = ?";
                    ps = conn.prepareStatement(query);
                    ps.setInt(1, assetId);
                    ps.setInt(2, userType.ordinal()+1);
                    if (ps.executeUpdate() > 0) {
                        System.out.println();
                        System.out.println("User And Asset Mapped Successfully !!!");
                        System.out.println();

                        //Checking for Unmapped Assets
                        query = "select userid from user where usertypeid = ?";
                        ps = conn.prepareStatement(query);
                        ps.setInt(1, userType.value);
                        ResultSet rs = ps.executeQuery();
                        while(rs.next()){
                            int userId = rs.getInt(1);
                            userManagement.checkRoleAndRetainAsset(userId, userType.ordinal()+1, assetManagement, userManagement);
                        }
                    }else{
                        System.out.println();
                        System.out.println("Error Occured !!!");
                        System.out.println();
                    }
                    break;
                default:
                    System.out.println();
                    System.out.println("Invalid choice !!!");
                    System.out.println();
                    break;
            }
        }
        displayAllAssets();
        DBConnection.closeConnection();
    }

    //Display assets on Request Raising
    public void displayAssetsOnRequest(int userId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT uam.assetid, a.assetname from user u join userassetmapping uam on u.usertypeid = uam.usertypeid join asset a on uam.assetid = a.assetid where userid = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int assetId = rs.getInt(1);
            String assetName = rs.getString(2);
            System.out.println("Asset Id : " + assetId + "\t AssetName : " + assetName + "\n");
        }
        DBConnection.closeConnection();
    }

    //Display All Time History
    public void displayAllTimeHistory() throws SQLException {
        System.out.println("***** ALL TIME HISTORY *****");
        Connection conn = DBConnection.getConnection();
        String query = "select aas.userid, u.username, aas.assetid, a.assetname, aas.datetime, aas.operations, \n" +
                "case\n" +
                "when aas.status = 1 then \"ACTIVE\"\n" +
                "when aas.status = 0 then \"INACTIVE\"\n" +
                "end as status\n" +
                "from assetassignmentssummary aas\n" +
                "join allusers u on aas.userid = u.userid join allassets a on aas.assetid = a.assetid;";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        String rowFormat = "%-10d %-20s %-10d %-20s %-25s %-20s %-10s\n";
        String headerFormat = "%-10s %-20s %-10s %-20s %-25s %-20s %-10s\n";
        System.out.println("*".repeat(130));
        System.out.printf(headerFormat, "USER-ID", "USERNAME", "ASSET-ID", "ASSETNAME", "DATE & TIME", "ASSIGN OR RETAIN", "ACTIVE OR INACTIVE");
        System.out.println("*".repeat(130));
        while(rs.next()){
            int userId = rs.getInt(1);
            String userName = rs.getString(2);
            int assetId = rs.getInt(3);
            String assetName = rs.getString(4);
            String datetime = rs.getString(5);
            String operations = rs.getString(6);
            String status = rs.getString(7);
            System.out.printf(rowFormat, userId, userName, assetId, assetName, datetime, operations, status);
        }
        System.out.println("*".repeat(130));
    }
}
