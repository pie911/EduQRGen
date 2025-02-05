// Placeholder for src/main/java/com/eduqrgen/database/QRDatabase.java
package com.eduqrgen.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles database operations for storing and checking QR codes.
 * Uses MySQL for persistent tracking of processed links.
 */
public class QRDatabase {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/eduqrgen";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your_password"; // 🔹 Replace with your actual MySQL password

    /**
     * Initializes the database by creating a table if it doesn't exist.
     */
    public QRDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            String createTableSQL = "CREATE TABLE IF NOT EXISTS qr_codes ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "url TEXT UNIQUE NOT NULL, "
                    + "qr_path TEXT NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ QR Code Database Initialized.");
        } catch (SQLException e) {
            System.err.println("❌ Database Initialization Failed: " + e.getMessage());
        }
    }

    /**
     * Checks if a URL already has a QR code in the database.
     *
     * @param url The URL to check
     * @return True if the QR code exists, false otherwise
     */
    public boolean isDuplicate(String url) {
        String query = "SELECT 1 FROM qr_codes WHERE url = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, url);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // If a record exists, it's a duplicate
        } catch (SQLException e) {
            System.err.println("❌ Error Checking Duplicate: " + e.getMessage());
        }
        return false;
    }

    /**
     * Saves a QR code record in the database.
     *
     * @param url The URL associated with the QR code
     * @param qrPath The file path of the generated QR code image
     */
    public void saveQRCode(String url, String qrPath) {
        if (isDuplicate(url)) {
            System.out.println("⚠️ QR Code for this URL already exists: " + url);
            return;
        }

        String insertSQL = "INSERT INTO qr_codes (url, qr_path) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
