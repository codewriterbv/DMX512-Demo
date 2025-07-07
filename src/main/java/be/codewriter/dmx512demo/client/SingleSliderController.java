package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXController;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import java.util.List;

public class SingleSliderController extends VBox {
    private final DMXController controller;
    private final List<DMXClient> clients;

    public SingleSliderController(DMXController controller, List<DMXClient> clients, String key, int defaultValue) {
        this.controller = controller;
        this.clients = clients;

        setAlignment(Pos.TOP_CENTER);
        setSpacing(10);

        var title = new Label(key);

        var slider = new Slider(0, 255, defaultValue);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(25);

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Convert slider value (0-100) to opacity (0-1)
            double sliderPercentage = slider.getValue() / slider.getMax();

            // Update the client values
            updateClients(key, (byte) (255.0 * sliderPercentage));
        });

        updateClients(key, (byte) (255.0 * (slider.getValue() / slider.getMax())));

        getChildren().addAll(title, slider);
    }

    private void updateClients(String key, byte value) {
        clients.stream()
                .filter(c -> c.hasChannel(key))
                .forEach(c -> c.setValue(key, value));

        controller.render(clients);
    }
}
