package be.codewriter.dmx512demo.component;

import be.codewriter.dmx512.controller.DMXChangeListener;
import be.codewriter.dmx512.controller.DMXChangeMessage;
import be.codewriter.dmx512.controller.DMXChangeNotifier;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OnOffIndicator extends HBox implements DMXChangeListener {
    private static final Logger LOGGER = LogManager.getLogger(OnOffIndicator.class.getName());
    public BooleanProperty isOn = new SimpleBooleanProperty(false);

    public OnOffIndicator(String label, DMXChangeNotifier controller) {
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);

        controller.addListener(this);

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

    @Override
    public void notify(DMXChangeMessage dmxChangeMessage, String value) {
        switch (dmxChangeMessage) {
            case CONNECTED -> isOn.set(true);
            case DISCONNECTED -> isOn.set(false);
            default -> LOGGER.debug("Received DMX change message is not handled: {}, {}", dmxChangeMessage, value);
        }
    }
}
