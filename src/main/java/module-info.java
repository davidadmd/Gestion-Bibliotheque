module com.example.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.library.controller to javafx.fxml;
    exports com.example.library;
}
