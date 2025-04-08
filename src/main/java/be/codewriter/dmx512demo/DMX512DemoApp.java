package be.codewriter.dmx512demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DMX512DemoApp extends Application {
    private static final Logger LOGGER = LogManager.getLogger(DMX512DemoApp.class.getName());
    
    public void run() {
        LOGGER.info("Starting UI...");
        launch();
    }

    @Override
    public void start(Stage stage) {
        var holder = new HBox(new Label("Test"));
        holder.setPadding(new Insets(10));
        holder.setSpacing(10);

        var scene = new Scene(holder, 1200, 850);
        stage.setScene(scene);
        stage.setTitle("DMX512 Demo");
        stage.setX(25);
        stage.setY(25);
        stage.show();

        stage.setOnCloseRequest(event -> {
            LOGGER.info("Closing application...");
            Platform.exit();
            System.exit(0);
        });
    }
}
