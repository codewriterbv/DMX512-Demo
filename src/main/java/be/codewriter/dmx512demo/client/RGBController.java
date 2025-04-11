package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXController;
import be.codewriter.dmx512demo.helper.ColorHelper;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.function.IntConsumer;

public class RGBController extends VBox {
    public RGBController(DMXController controller, List<DMXClient> clients) {
        var rgbColorBox = new RGBColorBox(255, 255, 255);
        var red = getSlider(controller, clients, rgbColorBox::setRed, "Red", Color.RED);
        var green = getSlider(controller, clients, rgbColorBox::setGreen, "Green", Color.GREEN);
        var blue = getSlider(controller, clients, rgbColorBox::setBlue, "Blue", Color.BLUE);
        var sliders = new HBox(red, green, blue);
        sliders.setAlignment(Pos.CENTER);
        getChildren().addAll(rgbColorBox, sliders);
    }

    private VBox getSlider(DMXController controller, List<DMXClient> clients, IntConsumer colorSetter, String key, Color color) {
        var holder = new VBox();
        holder.setPrefWidth(50);
        holder.setSpacing(10);
        holder.setAlignment(Pos.TOP_CENTER);
        holder.getChildren().add(new Label(key));

        // For this approach, a Pane or Region works better than a Box
        var colorBox = new StackPane();
        colorBox.setPrefHeight(20);

        // Apply style with border
        colorBox.setStyle("-fx-background-color: " + ColorHelper.getCssRGBA(color, 1) + ";"
                + "-fx-border-color: " + ColorHelper.getCssRGBA(color, 1) + ";"
                + "-fx-border-width: 2px;"
        );

        // Make colorBox fill the width of the holder
        colorBox.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(colorBox, new Insets(0, 0, 10, 0));

        holder.getChildren().add(colorBox);

        var slider = new Slider(0, 255, 255);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(25);
        holder.getChildren().add(slider);

        // Bind opacity of the colorBox material to the slider value
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Convert slider value (0-100) to opacity (0-1)
            double sliderPercentage = slider.getValue() / slider.getMax();

            // Update the style with new opacity
            colorBox.setStyle("-fx-background-color: " + ColorHelper.getCssRGBA(color, sliderPercentage) + ";"
                    + "-fx-border-color: " + ColorHelper.getCssRGBA(color, 1) + ";"
                    + "-fx-border-width: 2px;"
            );

            // Update the color value through the setter
            colorSetter.accept((int) (255.0 * sliderPercentage));

            // Update the client values
            clients.stream()
                    .filter(c -> c.hasChannel(key))
                    .forEach(c -> c.setValue(key, (byte) (255.0 * sliderPercentage)));

            controller.render(clients);
        });

        return holder;
    }

    private static class RGBColorBox extends StackPane {
        private int red;
        private int green;
        private int blue;

        public RGBColorBox(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            updateColor();
            setPrefHeight(20);
        }

        public void setRed(int red) {
            this.red = red;
            updateColor();
        }

        public void setGreen(int green) {
            this.green = green;
            updateColor();
        }

        public void setBlue(int blue) {
            this.blue = blue;
            updateColor();
        }

        private void updateColor() {
            setStyle("-fx-background-color: " + ColorHelper.getCssRGBA(red, green, blue, 1) + ";"
                    + "-fx-border-color: " + ColorHelper.getCssRGBA(Color.BLACK, 1) + ";"
                    + "-fx-border-width: 2px;"
            );
        }
    }
}
