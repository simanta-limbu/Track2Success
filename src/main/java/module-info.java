module com.example.track2success {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.track2success to javafx.fxml;
    exports com.example.track2success;
}