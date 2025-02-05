module com.eduqrgen {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.eduqrgen to javafx.fxml;
    exports com.eduqrgen;
}
