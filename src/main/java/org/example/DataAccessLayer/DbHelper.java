package org.example.DataAccessLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHelper {


    private static final String DB_URL = "jdbc:postgresql://localhost:5432/staj_yonetim_sistemi";
    private static final String USER = "postgres";
    private static final String PASS = "yusuf1945.";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}