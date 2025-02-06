module com.eduqrgen {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.swing;
    requires java.desktop;
    requires java.sql;
    requires java.management;
    requires org.apache.pdfbox;
    requires com.google.zxing;
    requires com.google.zxing.javase;

    opens com.eduqrgen to javafx.fxml;
    exports com.eduqrgen;
}