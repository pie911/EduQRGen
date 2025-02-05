// Placeholder for src/main/java/module-info.java
module com.eduqrgen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;          // ✅ Required for MySQL/SQLite database
    requires org.apache.pdfbox; // ✅ Required for PDF handling
    requires com.google.zxing;  // ✅ Required for QR Code generation
    requires java.desktop;      // ✅ Required for image processing and rendering

    opens com.eduqrgen to javafx.fxml;
    opens com.eduqrgen.database to java.sql; // Allows database access

    exports com.eduqrgen;
    exports com.eduqrgen.database;
}
