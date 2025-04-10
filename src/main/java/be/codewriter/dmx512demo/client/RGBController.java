package be.codewriter.dmx512demo.client;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class RGBController extends HBox {
    public RGBController() {
        var red = getSlider("Red", Color.RED);
        var green = getSlider("Green", Color.GREEN);
        var blue = getSlider("Blue", Color.BLUE);
        getChildren().addAll(red, green, blue);
    }

    private VBox getSlider(String title, Color color) {
        var holder = new VBox();
        holder.setSpacing(10);
        holder.getChildren().add(new Label(title));

        var colorBox = new Box(30, 20, 20); // Width, height, depth
        holder.getChildren().add(colorBox);

        // Create PhongMaterial with the given color and apply it to the Box
        PhongMaterial material = new PhongMaterial(color);
        colorBox.setMaterial(material);

        // Make colorBox expand to fill the width of the holder
        //colorBox.setWidth(Double.MAX_VALUE);
        VBox.setVgrow(colorBox, Priority.NEVER);
        VBox.setMargin(colorBox, new Insets(0, 0, 10, 0));

        var slider = new Slider();
        slider.setOrientation(Orientation.VERTICAL);
        slider.setMin(0);
        slider.setMax(255);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25);
        slider.setValue(255);
        holder.getChildren().add(slider);

        // Bind opacity of the colorBox material to the slider value
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Convert slider value (0-100) to opacity (0-1)
            double sliderPercentage = slider.getValue() / slider.getMax()
            double opacity = Math.min(1.0, Math.max(0.0, newVal.doubleValue() / 100.0));

            // Create a new color with the same RGB but different opacity
            Color adjustedColor = new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    opacity
            );

            material.setDiffuseColor(adjustedColor);
        });

        return holder;
    }
}
