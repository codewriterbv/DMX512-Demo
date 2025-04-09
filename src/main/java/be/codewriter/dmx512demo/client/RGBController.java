package be.codewriter.dmx512demo.client;

import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class RGBController extends VBox {
    public RGBController() {
        var slider = new Slider();
        slider.setOrientation(Orientation.VERTICAL);
        getChildren().add(slider);
    }
}
