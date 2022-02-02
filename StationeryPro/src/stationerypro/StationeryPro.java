package stationerypro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StationeryPro {
    private static Connection mysqlconfig;
    public static Connection configDB()throws SQLException {
        try {
            String url="jdbc:mysql://217.21.72.102:3306/u1694897_desktop_stationerypro";
            String user="u1694897_desktop_stationerypro";
            String pass="tif3xhibiti0n";
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            mysqlconfig=DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.err.println("Koneksi gagal "+e.getMessage());
        }
        return mysqlconfig;
    }
}
