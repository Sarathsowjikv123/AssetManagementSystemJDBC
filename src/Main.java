import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.*;
class Options{
    private static int userId, assetId, assetCount;
    private static String userName, assetName, assetType, userType;
    static boolean executeAdminOptions(Scanner sc, int option, AssetManagement assetManagement, UserManagement userManagement){
        switch (option){
            case 1:
                //Display All Assets
                try {
                    assetManagement.displayAllAssets();
                } catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 2:
                //Add New Asset
                System.out.println("Enter Asset Details : ");
                System.out.print("Asset Name : ");
                assetName = sc.nextLine();
                System.out.print("Asset Type (HARDWARE / SOFTWARE): ");
                try {
                    assetType = sc.nextLine();
                }catch(IllegalArgumentException e){
                    System.out.println("Invalid Asset Type");
                }
                assetType = assetType.toUpperCase();
                System.out.print("Asset Count : ");
                assetCount = sc.nextInt();
                try {
                    assetManagement.addAsset(assetName, Asset.AssetType.valueOf(assetType), assetCount);
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 3:
                //Allocate Asset To User
                try {
                    assetManagement.displayAllAssets();
                    userManagement.getUserList();
                    userManagement.displayIncompletedRequests();
                    System.out.print("Enter User Id : ");
                    userId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Asset Id : ");
                    assetId = sc.nextInt();
                    sc.nextLine();
                    userManagement.allocateAsset(userId, assetId, assetManagement, userManagement);
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 4:
                //Update Asset Information in the Inventory
                try {
                    assetManagement.displayAllAssets();
                }catch (SQLException e){
                    System.out.println(e);
                }
                System.out.println();
                System.out.print("Enter Asset ID : ");
                assetId = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter Asset Name : ");
                assetName = sc.nextLine();
                System.out.print("Enter Asset Type : ");
                assetType = sc.nextLine();
                assetType = assetType.toUpperCase();
                System.out.print("Enter Asset Count : ");
                assetCount = sc.nextInt();
                try{
                    assetManagement.updateAssetInventory(assetId, assetName, Asset.AssetType.valueOf(assetType), assetCount);
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 5:
                //Remove an Asset
                try{
                    assetManagement.displayAllAssets();
                } catch (SQLException e) {
                    System.out.println(e);
                }
                System.out.println();
                System.out.print("Enter Asset Id : ");
                assetId = sc.nextInt();
                sc.nextLine();
                try {
                    assetManagement.removeAnAsset(assetId);
                }catch (SQLException e){
                    System.out.println(e);
                }
            case 6:
                //Display User List
                try {
                    userManagement.getUserList();
                }catch (SQLException e){
                    System.out.println(e.getMessage());
                }
                viewAdminOptions();
                return true;
            case 7:
                //Display Asset Requirements for Each User
                try {
                    userManagement.getRequirements();
                }catch (SQLException e){
                    System.out.println(e.getMessage());
                }
                viewAdminOptions();
                return true;
            case 8:
                //Add New User
                System.out.println("Enter User Details : ");
                System.out.print("Enter User Name : ");
                userName = sc.nextLine();
                System.out.print("Enter New Password for the User: ");
                String newPassword = sc.nextLine();
                System.out.print("Enter User Type (MANAGER / EMPLOYEE / TRAINEE) : ");
                userType = sc.nextLine();
                userType = userType.toUpperCase();
                try {
                    userManagement.addUser(userName, User.UserType.valueOf(userType), newPassword, assetManagement, userManagement);
                }catch (Exception e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 9:
                //Retain an Asset
                System.out.print("Enter User Id : ");
                userId = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter Asset Id : ");
                assetId = sc.nextInt();
                sc.nextLine();
                assetManagement.retainAsset(userId, assetId, userManagement);
                viewAdminOptions();
                return true;
            case 10:
                try {
                    assetManagement.displayRetainedAssets();
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 11:
                //Remove User
                try{
                    userManagement.getUserList();
                } catch (SQLException e) {
                    System.out.println(e);
                }
                System.out.println();
                System.out.print("Enter User Id : ");
                userId = sc.nextInt();
                sc.nextLine();
                try {
                    userManagement.removeUser(userId);
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 12:
                // Update User and Asset Mapping
                try {
                    assetManagement.displayAllAssets();
                } catch (SQLException e) {
                    System.out.println(e);
                }
                System.out.print("Enter Asset Id : ");
                assetId = sc.nextInt();
                sc.nextLine();
                try {
                    assetManagement.updateAssetMapping(assetId, sc, userManagement, assetManagement);
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 13:
                //Update User Info and Roles
                try {
                    userManagement.getUserList();
                }catch (SQLException e){
                    System.out.println(e);
                }
                System.out.print("Enter User Id : ");
                userId = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter User Name : ");
                userName = sc.nextLine();
                System.out.print("Enter User Type (MANAGER / EMPLOYEE / TRAINEE) : ");
                userType = sc.nextLine();
                userType = userType.toUpperCase();
                try {
                    userManagement.updateUserInfoAndRoles(userId, userName, User.UserType.valueOf(userType), assetManagement, userManagement);
                } catch (SQLException e) {
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 14:
                //Display User Summary
                System.out.print("Enter User Id : ");
                userId = sc.nextInt();
                sc.nextLine();
                try {
                    userManagement.displayUserSummary(userId);
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 15:
                //Retain all assets of an User
                System.out.print("Enter User Id : ");
                userId = sc.nextInt();
                sc.nextLine();
                try {
                    assetManagement.retainAllAssetsOfUser(userId, userManagement);
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 16:
                //Display All Requests
                try {
                    userManagement.displayAllRequests();
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 17:
                //Display incompleted Requests
                try {
                    userManagement.displayIncompletedRequests();
                } catch (SQLException e) {
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 18:
                //Display Completed Requests
                try {
                    userManagement.displayCompletedRequests();
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 19:
                try{
                assetManagement.displayAllTimeHistory();
                }catch (SQLException e){
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 20:
                try{
                    userManagement.assetsAllocatedToUsers();
                } catch (SQLException e) {
                    System.out.println(e);
                }
                viewAdminOptions();
                return true;
            case 21:
                //Exit
                System.out.println("Thank You !!!");
                System.out.println();
                return false;
            default:
                System.out.println("Invalid option !!!");
                System.out.println();
                return true;
        }
    }

    static boolean executeUserOptions(Scanner sc, int option,int userId, AssetManagement assetManagement, UserManagement userManagement){
        switch (option){
            case 1:
//                System.out.print("Enter User ID : ");
//                userId = sc.nextInt();
//                sc.nextLine();
                try {
                    userManagement.getUserByID(userId);
                }catch (SQLException e){
                    System.out.println(e);
                }
                System.out.println("************************");
                System.out.println("**** WELCOME USER "+userId+" ****");
                System.out.println("************************");
                viewUserOptions();
                return true;
            case 2:
//                System.out.print("Enter User Id : ");
//                userId = sc.nextInt();
//                sc.nextLine();
                try {
                    userManagement.displayUserSummary(userId);
                }catch (SQLException e){
                    System.out.println(e);
                }
                System.out.println("************************");
                System.out.println("**** WELCOME USER "+userId+" ****");
                System.out.println("************************");
                viewUserOptions();
                return true;
            case 3:
                try{
                    userManagement.getUserByID(userId);
                    System.out.println("**** ASSETS ****");
                    System.out.println();
                    assetManagement.displayAssetsOnRequest(userId);
                    //assetManagement.displayAllAssets();
                    System.out.println("Note : ");
                    System.out.println("Only request can be made for the above mentioned assets based on User Type.");
                    System.out.println("Cannot make Assign request for an asset that was Already Allocated.");
                } catch (SQLException e) {
                    System.out.println(e);
                }
                System.out.println("*************************");
                System.out.println("     RAISE A REQUEST     ");
                System.out.println("*************************");
                //userId = sc.nextInt();
                //sc.nextLine();
                System.out.print("Enter Asset Id : ");
                assetId = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter Operation (1 Assign / 2 Retain) : ");
                int operationId = sc.nextInt();
                String operation="";
                sc.nextLine();
                if(operationId == 1){
                    operation += "ASSIGN";
                }else if(operationId == 2){
                    operation += "RETAIN";
                }else {

                    System.out.println("Invalid Option !!!");

                    System.out.println("************************");
                    System.out.println("**** WELCOME USER "+userId+" ****");
                    System.out.println("************************");
                    viewUserOptions();
                    return true;
                }
                try {
                    userManagement.raiseRequest(userId, assetId, operation);
                }catch (SQLException e){
                    System.out.println(e);
                }
                System.out.println("************************");
                System.out.println("**** WELCOME USER "+userId+" ****");
                System.out.println("************************");
                viewUserOptions();
                return true;
            case 4:
                System.out.println("Thank You !!!");
                System.out.println();
                return false;
            default:
                System.out.print("Invalid option !!!");
                System.out.println();
                return true;
        }
    }

    static boolean executeMainOptions(Scanner sc, int option, AssetManagement assetManagement, UserManagement userManagement) throws SQLException {
        switch(option){
            case 1:
                Options.viewAdminOptions();
                do{
                    System.out.print("Enter Your Option: ");
                    option = sc.nextInt();
                    sc.nextLine();
                }while(Options.executeAdminOptions(sc, option, assetManagement, userManagement));
                Options.viewMainOptions();
                return true;
            case 2:
                System.out.print("Enter Your User ID : ");
                userId = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter Your Password : ");
                String password = sc.nextLine();
                if(!userManagement.isUserAvailable(userId, password)){
                    System.out.println();
                    System.out.println("Invalid User Id & Password !!!");
                    System.out.println();
                    Options.viewMainOptions();
                    return true;
                }
                System.out.println("************************");
                System.out.println("**** WELCOME USER "+userId+" ****");
                System.out.println("************************");
                Options.viewUserOptions();
                do{
                    System.out.print("Enter Your Option: ");
                    option = sc.nextInt();
                    sc.nextLine();
                }while(Options.executeUserOptions(sc, option, userId, assetManagement, userManagement));
                Options.viewMainOptions();
                return true;
            case 3:
                System.out.println("Thank You !!!");
                System.out.println();
                return false;
            default:
                System.out.println("Invalid option !!!");
                System.out.println();
                return true;
        }
    }

    static void viewAdminOptions(){
        System.out.println("*************************");
        System.out.println("***** WELCOME ADMIN *****");
        System.out.println("*************************");
        System.out.println("1.View Assets.");
        System.out.println("2.Add New Asset.");
        System.out.println("3.Allocate Asset.");
        System.out.println("4.Update Asset Inventory.");
        System.out.println("5.Remove an Asset.");
        System.out.println("6.Users List.");
        System.out.println("7.See All Possible Assignments.");
        System.out.println("8.Add New User.");
        System.out.println("9.Retain Asset.");
        System.out.println("10.Display Retained Assets List.");
        System.out.println("11.Remove User.");
        System.out.println("12.Update Asset Mapping.");
        System.out.println("13.Update User info and Roles.");
        System.out.println("14 Display Allocations and Retentions of an User.");
        System.out.println("15 Retain All Assets of An User.");
        System.out.println("16.Display All Requests.");
        System.out.println("17.Display Incomplete Requests.");
        System.out.println("18.Display Completed Requests.");
        System.out.println("19.View All Time History.");
        System.out.println("20.List of assets allocated to Users.");
        System.out.println("21.Logout.");
        System.out.println();
    }

    static void viewUserOptions() {
        System.out.println("1.VIEW SUMMARY.");
        System.out.println("2.VIEW ALL ALLOCATIONS AND RETENTIONS.");
        System.out.println("3.RAISE A REQUEST.");
        System.out.println("4.LOGOUT.");
        System.out.println();
    }

    static void viewMainOptions() {
        System.out.println("***********************************");
        System.out.println("***** ASSET MANAGEMENT SYSTEM *****");
        System.out.println("***********************************");
        System.out.println("1.Admin.");
        System.out.println("2.User.");
        System.out.println("3.Exit.");
        System.out.println();
    }

    static void prePopulateAsset(String assetName, Asset.AssetType assetType, int assetCount, AssetManagement assetManagement, UserManagement userManagement) throws SQLException{
        boolean result;
        Connection conn = DBConnection.getConnection();
        String query = "SELECT 1 WHERE EXISTS (SELECT assetname, assettype FROM asset where assetname = ? and assettype = ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, assetName);
        ps.setString(2, assetType.toString());
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            result = true;
        }else{
            result = false;
        }
        if(!result){
            assetManagement.addAsset(assetName, assetType, assetCount);
        }
        DBConnection.closeConnection();
    }

    static void prePopulateUser(String userName, User.UserType userType,String password, AssetManagement assetManagement, UserManagement userManagement) throws SQLException{
        boolean result;
        Connection conn = DBConnection.getConnection();
        String query = "SELECT 1 WHERE EXISTS (SELECT userTypeId, username FROM user where usertypeid = ? and username = ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userType.value);
        ps.setString(2, userName);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            result = true;
        }
        else{
            result = false;
        }
        if(!result){
            userManagement.addUser(userName, userType, password, assetManagement, userManagement);
        }
        DBConnection.closeConnection();
    }
}

public class Main {
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        AssetManagement assetManagement = new AssetManagement();
        UserManagement userManagement = new UserManagement();
        Options.prePopulateAsset("LAPTOP", Asset.AssetType.HARDWARE, 100, assetManagement, userManagement);
        Options.prePopulateAsset("Phone", Asset.AssetType.HARDWARE, 75, assetManagement, userManagement);
        Options.prePopulateAsset("Antivirus", Asset.AssetType.SOFTWARE, 30, assetManagement, userManagement);
        Options.prePopulateUser("Sample User 1", User.UserType.MANAGER,"sampleuser@123", assetManagement, userManagement);
        Options.prePopulateUser("Sample User 2", User.UserType.EMPLOYEE,"sampleuser@123", assetManagement, userManagement);
        Options.prePopulateUser("Sample User 3", User.UserType.TRAINEE,"sampleuser@123", assetManagement, userManagement);
        Options.viewMainOptions();
        int option;
        do{
            System.out.print("Enter Your Option: ");
            option = sc.nextInt();
            sc.nextLine();
        }while(Options.executeMainOptions(sc, option, assetManagement, userManagement));
    }
}