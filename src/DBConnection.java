import java.sql.*;
public class DBConnection {
    private static final String url = "jdbc:mysql://localhost:3306/asm1";
    private static final String user = "root";
    private static final String password = "Sarath@0508";
    private static Connection conn = null;

    public static Connection getConnection() throws SQLException{
        if(conn == null || conn.isClosed()){
            conn = DriverManager.getConnection(url, user, password);
        }
        return conn;
    }

    public static ResultSet executeQuery(String query) throws SQLException{
        Statement stmt = getConnection().createStatement();
        return stmt.executeQuery(query);
    }

    public static void closeConnection() throws SQLException{
        if(conn != null & !conn.isClosed()){
            conn.close();
        }
    }
}
