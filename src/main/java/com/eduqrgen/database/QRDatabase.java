package com.eduqrgen.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QRDatabase {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "eduqrgen";
    private static final String DB_USER = "yash";
    private static final String DB_PASSWORD = "Hero";

    public QRDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(createDatabaseSQL);

            try (Connection dbConn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
                 Statement dbStmt = dbConn.createStatement()) {

                String createTableSQL = "CREATE TABLE IF NOT EXISTS qr_codes ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "url TEXT UNIQUE NOT NULL, "
                        + "qr_path TEXT NOT NULL, "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                dbStmt.executeUpdate(createTableSQL);
                System.out.println("✅ QR Code Database Initialized.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Database Initialization Failed: " + e.getMessage());
        }
    }

    public boolean isDuplicate(String url) {
        String query = "SELECT 1 FROM qr_codes WHERE url = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, url);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("❌ Error Checking Duplicate: " + e.getMessage());
        }
        return false;
    }

    public void saveQRCode(String url, String qrPath) {
        if (isDuplicate(url)) {
            System.out.println("⚠️ QR Code for this URL already exists: " + url);
            return;
        }

        String insertSQL = "INSERT INTO qr_codes (url, qr_path) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, url);
            pstmt.setString(2, qrPath);
            pstmt.executeUpdate();
            System.out.println("✅ QR Code Saved for: " + url);
        } catch (SQLException e) {
            System.err.println("❌ Error Saving QR Code: " + e.getMessage());
        }
    }
}