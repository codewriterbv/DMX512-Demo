package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PanTiltController extends VBox {
    private static final Logger LOGGER = LogManager.getLogger(PanTiltController.class.getName());
    private static final double AREA_SIZE = 200;
    private static final double KNOB_RADIUS = 15;
    private final DoubleProperty panValue = new SimpleDoubleProperty(127);
    private final DoubleProperty tiltValue = new SimpleDoubleProperty(127);
    private final DoubleProperty speedValue = new SimpleDoubleProperty(127);
    private final Circle joystick;
    private final Pane joystickArea;
    private final Label valueLabel;
    private boolean isDragging = false;

    public PanTiltController(DMXController controller, List<DMXClient> clients) {
        setSpacing(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));

        // Create value label
        valueLabel = new Label();
        valueLabel.setStyle("-fx-font-size: 12px;");

        // Create the joystick area
        joystickArea = new Pane();
        joystickArea.setPrefSize(AREA_SIZE, AREA_SIZE);
        joystickArea.setMaxSize(AREA_SIZE, AREA_SIZE);
        joystickArea.setMinSize(AREA_SIZE, AREA_SIZE);
        joystickArea.getStyleClass().add("joystick-boundary");

        // Create the joystick knob
        joystick = new Circle(KNOB_RADIUS);
        joystick.getStyleClass().add("joystick-knob");

        // Position joystick at center initially
        moveJoystickTo(AREA_SIZE / 2, AREA_SIZE / 2);

        // Add joystick to the area
        joystickArea.getChildren().add(joystick);

        // Setup mouse events
        setupMouseHandling();

        // Create speed slider
        Slider speedSlider = new Slider(0, 255, 127);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(25);

        // Bind speed value to slider
        speedValue.bind(speedSlider.valueProperty());

        // Add components to layout
        getChildren().addAll(joystickArea, speedSlider, valueLabel);

        // Add CSS styles
        try {
            getStylesheets().add(getClass().getResource("/css/panTiltControl.css").toExternalForm());
        } catch (Exception e) {
            LOGGER.error("Error while loading stylesheets: {}", e.getMessage());
        }

        // Send value changes to devices
        panValue.addListener((_, _, _) -> updateClients(controller, clients));
        tiltValue.addListener((_, _, _) -> updateClients(controller, clients));
        speedSlider.valueProperty().addListener((_, _, _) -> updateClients(controller, clients));

        // Send initial values
        updateClients(controller, clients);
    }

    private void updateClients(DMXController controller, List<DMXClient> clients) {
        valueLabel.setText(String.format("Pan: %.0f, Tilt: %.0f, Speed: %.0f",
                panValue.getValue(), tiltValue.getValue(), speedValue.getValue()));
        clients.stream()
                .filter(c -> c.hasChannel("pan"))
                .forEach(c -> c.setValue("pan", panValue.getValue().byteValue()));
        clients.stream()
                .filter(c -> c.hasChannel("tilt"))
                .forEach(c -> c.setValue("tilt", tiltValue.getValue().byteValue()));
        clients.stream()
                .filter(c -> c.hasChannel("Pan/Tilt Speed"))
                .forEach(c -> c.setValue("Pan/Tilt Speed", speedValue.getValue().byteValue()));
        controller.render(clients);
    }

    private void setupMouseHandling() {
        joystick.setOnMousePressed(event -> {
            isDragging = true;
            event.consume();
        });

        joystick.setOnMouseReleased(event -> {
            isDragging = false;
            event.consume();
        });

        joystickArea.setOnMouseDragged(event -> {
            if (!isDragging) {
                return;
            }

            double mouseX = event.getX();
            double mouseY = event.getY();

            // Constrain to boundary
            mouseX = Math.max(KNOB_RADIUS, Math.min(mouseX, AREA_SIZE - KNOB_RADIUS));
            mouseY = Math.max(KNOB_RADIUS, Math.min(mouseY, AREA_SIZE - KNOB_RADIUS));

            moveJoystickTo(mouseX, mouseY);
            event.consume();
        });

        joystickArea.setOnMousePressed(event -> {
            // Allow clicking anywhere in the area to move joystick
            double mouseX = event.getX();
            double mouseY = event.getY();

            // Constrain to boundary
            mouseX = Math.max(KNOB_RADIUS, Math.min(mouseX, AREA_SIZE - KNOB_RADIUS));
            mouseY = Math.max(KNOB_RADIUS, Math.min(mouseY, AREA_SIZE - KNOB_RADIUS));

            moveJoystickTo(mouseX, mouseY);
            isDragging = true;
            event.consume();
        });
    }

    private void moveJoystickTo(double x, double y) {
        joystick.setCenterX(x);
        joystick.setCenterY(y);

        // Convert position to DMX values (0-255)
        // Adjust for knob radius in calculations
        double normalizedX = (x - KNOB_RADIUS) / (AREA_SIZE - 2 * KNOB_RADIUS);
        double normalizedY = (y - KNOB_RADIUS) / (AREA_SIZE - 2 * KNOB_RADIUS);

        panValue.set(normalizedX * 255);
        tiltValue.set(normalizedY * 255);
    }
}