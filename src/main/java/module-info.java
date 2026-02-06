module com.videoconverter {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.videoconverter to javafx.fxml;
    exports com.videoconverter;
}