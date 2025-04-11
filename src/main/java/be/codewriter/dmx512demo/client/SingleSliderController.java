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
    public SingleSliderController(DMXController controller, List<DMXClient> clients, String key, int defaultValue) {
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
            clients.stream()
                    .filter(c -> c.hasChannel(key))
                    .forEach(c -> c.setValue(key, (byte) (255.0 * sliderPercentage)));

            controller.render(clients);
        });

        getChildren().addAll(title, slider);
    }
}
