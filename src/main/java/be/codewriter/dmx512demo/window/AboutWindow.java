package be.codewriter.dmx512demo.window;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AboutWindow {
    private final BorderPane layout;
    private Stage stage;

    public AboutWindow(Stage parentStage) {
        layout = new BorderPane();
        layout.setPadding(new Insets(10));

        createWindow(parentStage);
    }

    private void createWindow(Stage parentStage) {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        stage.setTitle("About DMX512 Demo");
        stage.setResizable(false);
        layout.setPadding(new Insets(10));

        var textArea = new TextArea("""
                This demo application shows how DMX Fixture files from the Open Fixture Library (OFL) (https://open-fixture-library.org/) can be loaded into Java Objects.
                
                Using these fixtures, you can control your DMX-capable lighting setups using the Java DMX512 library created by CodeWriter.
                
                More information about the library can be found at:
                https://github.com/codewriterbv/DMX512/
                """);
        textArea.setWrapText(true);
        textArea.setEditable(false);

        layout.setCenter(textArea);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        buttonBox.getChildren().add(closeButton);
        layout.setTop(buttonBox);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setHeight(400);
        stage.setWidth(600);
    }

    public void show() {
        stage.show();
    }
}