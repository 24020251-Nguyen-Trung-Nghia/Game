module org.example.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens org.example.game to javafx.fxml;
    exports org.example.game;
}