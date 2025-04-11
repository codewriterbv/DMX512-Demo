package be.codewriter.dmx512demo.component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class OnOffIndicator extends HBox {
    public BooleanProperty isOn = new SimpleBooleanProperty(false);

    public OnOffIndicator(String label) {
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);

        // Create the indicator circle
        Circle connectionIndicator = new Circle(10);
        connectionIndicator.setFill(Color.RED);
        getChildren().addAll(connectionIndicator, new Label(label));

        // Add glow effect to make it look more like an LED
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(2.0);
        connectionIndicator.setEffect(innerShadow);

        // Optional: add a style for better visibility
        connectionIndicator.setStroke(Color.DARKGRAY);
        connectionIndicator.setStrokeWidth(0.5);

        connectionIndicator.fillProperty().bind(
                Bindings.when(isOn).then(Color.GREEN).otherwise(Color.RED)
        );
    }
}
