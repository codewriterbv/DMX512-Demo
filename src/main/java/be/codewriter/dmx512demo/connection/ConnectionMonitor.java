package be.codewriter.dmx512demo.connection;

import be.codewriter.dmx512.controller.DMXController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ConnectionMonitor extends HBox {
    public ConnectionMonitor(DMXController controller) {
        this.getChildren().addAll(new Label(controller.getType() + " connection " + controller.getAddress()),
                new OnOffIndicator("Is connected", controller));
        this.setPrefWidth(250);
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);
    }
}
