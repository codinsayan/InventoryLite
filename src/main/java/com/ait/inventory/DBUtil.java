package com.ait.inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static String jdbcUrl;
    private static String jdbcUser;
    private static String jdbcPass;

    static {
        try {
            // Explicitly register MySQL driver (helps if auto-loading fails)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // You can log this if you have a logger
        }
    }

    public static void configure(String url, String user, String pass) {
        jdbcUrl = url;
        jdbcUser = user;
        jdbcPass = pass;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass);
    }
}
