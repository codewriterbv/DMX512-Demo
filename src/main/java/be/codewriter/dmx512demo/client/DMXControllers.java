package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXController;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DMXControllers extends GridPane {
    private static final Logger LOGGER = LogManager.getLogger(DMXControllers.class.getName());

    public DMXControllers(DMXController controller, DMXClient... clients) {
        this.setPadding(new Insets(15, 0, 0, 0));

        add(new RGBController(), 0, 0);
    }
}