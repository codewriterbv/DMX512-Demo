package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.controller.DMXController;
import be.codewriter.dmx512.model.DMXUniverse;
import be.codewriter.dmx512.ofl.model.Fixture;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class SingleSliderController extends VBox {
    private final DMXController controller;
    private final DMXUniverse universe;
    private final Fixture fixture;

    public SingleSliderController(DMXController controller, DMXUniverse universe, Fixture fixture, String key, int defaultValue, Orientation orientation) {
        this.controller = controller;
        this.universe = universe;
        this.fixture = fixture;

        setAlignment(Pos.TOP_CENTER);
        setSpacing(10);

        var title = new Label(key);

        var valueLabel = new Label(String.valueOf(defaultValue));
        valueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        var slider = new Slider(0, 255, defaultValue);
        slider.setOrientation(orientation);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(25);

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Update the value label
            valueLabel.setText(String.format("%.0f", newVal.doubleValue()));

            // Convert slider value (0-100) to opacity (0-1)
            double sliderPercentage = slider.getValue() / slider.getMax();

            // Update the client values
            updateClients(key, (byte) (255.0 * sliderPercentage));
        });

        updateClients(key, (byte) (255.0 * (slider.getValue() / slider.getMax())));

        getChildren().addAll(title, slider, valueLabel);
    }

    private void updateClients(String key, byte value) {
        universe.updateFixtures(fixture, key, value);
        controller.render(universe);
    }
}
