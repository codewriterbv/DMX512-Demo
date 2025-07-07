package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

public class PanTiltController extends VBox {
    private static final Logger LOGGER = LogManager.getLogger(PanTiltController.class.getName());
    private static final double AREA_SIZE = 200;
    private static final double KNOB_RADIUS = 15;
    private static final double MIN_ANIMATION_AREA_SIZE = 20;

    private final DMXController controller;
    private final List<DMXClient> clients;
    private final DoubleProperty panValue = new SimpleDoubleProperty(127);
    private final DoubleProperty tiltValue = new SimpleDoubleProperty(127);
    private final DoubleProperty animationSpeedValue = new SimpleDoubleProperty(50);
    private final BooleanProperty autoAnimationEnabled = new SimpleBooleanProperty(false);

    private final Circle joystick;
    private final Pane joystickArea;
    private final Rectangle animationArea;
    private final Label valueLabel;
    private final Button autoAnimationButton;
    private final Slider speedSlider;
    private final Slider animationSpeedSlider;
    private final Random random = new Random();
    private boolean isDragging = false;
    private boolean isResizingAnimationArea = false;
    private String resizeMode = "";
    private Timeline animationTimeline;
    private double targetX, targetY; // Target position for animation
    private double currentX, currentY; // Current position during animation

    public PanTiltController(DMXController controller, List<DMXClient> clients) {
        this.controller = controller;
        this.clients = clients;

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

        // Create the animation area rectangle
        animationArea = new Rectangle();
        animationArea.setStroke(Color.BLUE);
        animationArea.setStrokeWidth(2);
        animationArea.getStyleClass().add("animation-area");

        // Initialize animation area to center quarter of joystick area
        double initialSize = AREA_SIZE / 2;
        double initialX = (AREA_SIZE - initialSize) / 2;
        double initialY = (AREA_SIZE - initialSize) / 2;
        animationArea.setX(initialX);
        animationArea.setY(initialY);
        animationArea.setWidth(initialSize);
        animationArea.setHeight(initialSize);

        // Create the joystick knob
        joystick = new Circle(KNOB_RADIUS);
        joystick.getStyleClass().add("joystick-knob");

        // Position joystick at center initially
        moveJoystickTo(AREA_SIZE / 2, AREA_SIZE / 2);

        // Add joystick to the area
        joystickArea.getChildren().addAll(animationArea, joystick);

        // Setup mouse events
        setupMouseHandling();

        // Create speed slider
        speedSlider = new Slider(0, 255, 127);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(25);

        // Create auto-animation controls
        autoAnimationButton = new Button("Enable Auto-Animation");
        autoAnimationButton.setOnAction(e -> toggleAutoAnimation());

        animationSpeedSlider = new Slider(1, 100, 50);
        animationSpeedSlider.setShowTickLabels(true);
        animationSpeedSlider.setShowTickMarks(true);
        animationSpeedSlider.setMajorTickUnit(25);
        animationSpeedSlider.disableProperty().bind(autoAnimationEnabled.not());
        animationSpeedValue.bind(animationSpeedSlider.valueProperty());

        Label animationSpeedLabel = new Label("Animation Speed:");
        animationSpeedLabel.disableProperty().bind(autoAnimationEnabled.not());

        // Add components to layout
        getChildren().addAll(
                joystickArea,
                speedSlider,
                autoAnimationButton,
                animationSpeedLabel,
                animationSpeedSlider,
                valueLabel
        );

        // Add CSS styles
        try {
            getStylesheets().add(getClass().getResource("/css/panTiltControl.css").toExternalForm());
        } catch (Exception e) {
            LOGGER.error("Error while loading stylesheets: {}", e.getMessage());
        }

        // Send value changes to devices
        panValue.addListener((_, _, _) -> updateClients());
        tiltValue.addListener((_, _, _) -> updateClients());
        speedSlider.valueProperty().addListener((_, _, _) -> updateClients());

        // Send initial values
        updateClients();
    }

    private void toggleAutoAnimation() {
        if (autoAnimationEnabled.get()) {
            stopAutoAnimation();
        } else {
            startAutoAnimation();
            speedSlider.setValue(0);
        }
    }

    private void startAutoAnimation() {
        autoAnimationEnabled.set(true);
        autoAnimationButton.setText("Disable Auto-Animation");

        // Set initial position to current joystick position
        currentX = joystick.getCenterX();
        currentY = joystick.getCenterY();

        // Generate first random target
        generateRandomTarget();

        animationTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> updateAnimation()));
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();
    }

    private void generateRandomTarget() {
        double areaX = animationArea.getX();
        double areaY = animationArea.getY();
        double areaWidth = animationArea.getWidth();
        double areaHeight = animationArea.getHeight();

        // Generate random target within animation area bounds
        targetX = areaX + (random.nextDouble() * areaWidth);
        targetY = areaY + (random.nextDouble() * areaHeight);

        // Ensure target is within joystick bounds
        targetX = Math.max(KNOB_RADIUS, Math.min(targetX, AREA_SIZE - KNOB_RADIUS));
        targetY = Math.max(KNOB_RADIUS, Math.min(targetY, AREA_SIZE - KNOB_RADIUS));
    }

    private void updateAnimation() {
        double speed = animationSpeedValue.get() / 1000.0; // Convert to reasonable animation speed

        // Calculate direction to target
        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance < 2.0) {
            // Reached target, generate new random target
            generateRandomTarget();
            return;
        }

        // Move towards target
        double normalizedDeltaX = deltaX / distance;
        double normalizedDeltaY = deltaY / distance;

        double moveDistance = speed * 20; // Adjust multiplier for desired speed
        currentX += normalizedDeltaX * moveDistance;
        currentY += normalizedDeltaY * moveDistance;

        // Ensure current position stays within bounds
        currentX = Math.max(KNOB_RADIUS, Math.min(currentX, AREA_SIZE - KNOB_RADIUS));
        currentY = Math.max(KNOB_RADIUS, Math.min(currentY, AREA_SIZE - KNOB_RADIUS));

        moveJoystickTo(currentX, currentY);
    }


    private void stopAutoAnimation() {
        autoAnimationEnabled.set(false);
        autoAnimationButton.setText("Enable Auto-Animation");

        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline = null;
        }
    }

    private void setupMouseHandling() {
        // Joystick handling
        joystick.setOnMousePressed(event -> {
            if (!autoAnimationEnabled.get()) {
                isDragging = true;
            }
            event.consume();
        });

        joystick.setOnMouseReleased(event -> {
            isDragging = false;
            event.consume();
        });

        // Animation area resizing - remove these as they interfere with joystickArea events
        // animationArea.setOnMousePressed and animationArea.setOnMouseReleased should be removed

        joystickArea.setOnMouseDragged(event -> {
            if (isDragging && !autoAnimationEnabled.get()) {
                double mouseX = event.getX();
                double mouseY = event.getY();

                // Constrain to boundary
                mouseX = Math.max(KNOB_RADIUS, Math.min(mouseX, AREA_SIZE - KNOB_RADIUS));
                mouseY = Math.max(KNOB_RADIUS, Math.min(mouseY, AREA_SIZE - KNOB_RADIUS));

                moveJoystickTo(mouseX, mouseY);
            } else if (isResizingAnimationArea && !autoAnimationEnabled.get()) {
                resizeAnimationArea(event.getX(), event.getY());
            }
            event.consume();
        });

        joystickArea.setOnMousePressed(event -> {
            if (!autoAnimationEnabled.get()) {
                double mouseX = event.getX();
                double mouseY = event.getY();

                // Check if clicking on animation area border for resizing
                if (isNearAnimationAreaBorder(mouseX, mouseY)) {
                    isResizingAnimationArea = true;
                } else {
                    // Allow clicking anywhere in the area to move joystick
                    mouseX = Math.max(KNOB_RADIUS, Math.min(mouseX, AREA_SIZE - KNOB_RADIUS));
                    mouseY = Math.max(KNOB_RADIUS, Math.min(mouseY, AREA_SIZE - KNOB_RADIUS));

                    moveJoystickTo(mouseX, mouseY);
                    isDragging = true;
                }
            }
            event.consume();
        });

        joystickArea.setOnMouseReleased(event -> {
            isDragging = false;
            isResizingAnimationArea = false;
            resizeMode = ""; // Clear resize mode
            event.consume();
        });
    }

    private boolean isNearAnimationAreaBorder(double mouseX, double mouseY) {
        double tolerance = 10;
        double areaX = animationArea.getX();
        double areaY = animationArea.getY();
        double areaWidth = animationArea.getWidth();
        double areaHeight = animationArea.getHeight();

        // Check corners first (higher priority)
        if (Math.abs(mouseX - areaX) < tolerance && Math.abs(mouseY - areaY) < tolerance) {
            resizeMode = "TOP_LEFT";
            return true;
        }
        if (Math.abs(mouseX - (areaX + areaWidth)) < tolerance && Math.abs(mouseY - areaY) < tolerance) {
            resizeMode = "TOP_RIGHT";
            return true;
        }
        if (Math.abs(mouseX - areaX) < tolerance && Math.abs(mouseY - (areaY + areaHeight)) < tolerance) {
            resizeMode = "BOTTOM_LEFT";
            return true;
        }
        if (Math.abs(mouseX - (areaX + areaWidth)) < tolerance && Math.abs(mouseY - (areaY + areaHeight)) < tolerance) {
            resizeMode = "BOTTOM_RIGHT";
            return true;
        }

        // Check edges
        if (Math.abs(mouseX - areaX) < tolerance && mouseY >= areaY && mouseY <= areaY + areaHeight) {
            resizeMode = "LEFT";
            return true;
        }
        if (Math.abs(mouseX - (areaX + areaWidth)) < tolerance && mouseY >= areaY && mouseY <= areaY + areaHeight) {
            resizeMode = "RIGHT";
            return true;
        }
        if (Math.abs(mouseY - areaY) < tolerance && mouseX >= areaX && mouseX <= areaX + areaWidth) {
            resizeMode = "TOP";
            return true;
        }
        if (Math.abs(mouseY - (areaY + areaHeight)) < tolerance && mouseX >= areaX && mouseX <= areaX + areaWidth) {
            resizeMode = "BOTTOM";
            return true;
        }

        return false;
    }

    private void resizeAnimationArea(double mouseX, double mouseY) {
        double currentX = animationArea.getX();
        double currentY = animationArea.getY();
        double currentWidth = animationArea.getWidth();
        double currentHeight = animationArea.getHeight();

        double newX = currentX;
        double newY = currentY;
        double newWidth = currentWidth;
        double newHeight = currentHeight;

        switch (resizeMode) {
            case "TOP_LEFT":
                newX = Math.min(mouseX, currentX + currentWidth - MIN_ANIMATION_AREA_SIZE);
                newY = Math.min(mouseY, currentY + currentHeight - MIN_ANIMATION_AREA_SIZE);
                newWidth = currentX + currentWidth - newX;
                newHeight = currentY + currentHeight - newY;
                break;

            case "TOP_RIGHT":
                newY = Math.min(mouseY, currentY + currentHeight - MIN_ANIMATION_AREA_SIZE);
                newWidth = Math.max(MIN_ANIMATION_AREA_SIZE, mouseX - currentX);
                newHeight = currentY + currentHeight - newY;
                break;

            case "BOTTOM_LEFT":
                newX = Math.min(mouseX, currentX + currentWidth - MIN_ANIMATION_AREA_SIZE);
                newWidth = currentX + currentWidth - newX;
                newHeight = Math.max(MIN_ANIMATION_AREA_SIZE, mouseY - currentY);
                break;

            case "BOTTOM_RIGHT":
                newWidth = Math.max(MIN_ANIMATION_AREA_SIZE, mouseX - currentX);
                newHeight = Math.max(MIN_ANIMATION_AREA_SIZE, mouseY - currentY);
                break;

            case "LEFT":
                newX = Math.min(mouseX, currentX + currentWidth - MIN_ANIMATION_AREA_SIZE);
                newWidth = currentX + currentWidth - newX;
                break;

            case "RIGHT":
                newWidth = Math.max(MIN_ANIMATION_AREA_SIZE, mouseX - currentX);
                break;

            case "TOP":
                newY = Math.min(mouseY, currentY + currentHeight - MIN_ANIMATION_AREA_SIZE);
                newHeight = currentY + currentHeight - newY;
                break;

            case "BOTTOM":
                newHeight = Math.max(MIN_ANIMATION_AREA_SIZE, mouseY - currentY);
                break;
        }

        // Ensure the animation area stays within joystick bounds
        newX = Math.max(0, Math.min(newX, AREA_SIZE - MIN_ANIMATION_AREA_SIZE));
        newY = Math.max(0, Math.min(newY, AREA_SIZE - MIN_ANIMATION_AREA_SIZE));
        newWidth = Math.min(newWidth, AREA_SIZE - newX);
        newHeight = Math.min(newHeight, AREA_SIZE - newY);

        animationArea.setX(newX);
        animationArea.setY(newY);
        animationArea.setWidth(newWidth);
        animationArea.setHeight(newHeight);
    }


    private void moveJoystickTo(double x, double y) {
        joystick.setCenterX(x);
        joystick.setCenterY(y);

        // Convert position to DMX values (0-65535)
        // Adjust for knob radius in calculations
        double normalizedX = (x - KNOB_RADIUS) / (AREA_SIZE - 2 * KNOB_RADIUS);
        double normalizedY = (y - KNOB_RADIUS) / (AREA_SIZE - 2 * KNOB_RADIUS);

        panValue.set(normalizedX * 65535);
        tiltValue.set(normalizedY * 65535);
    }

    private void updateClients() {
        var currentPanValue = panValue.getValue().intValue();
        int panCoarse = currentPanValue / 256;
        int panFine = currentPanValue % 256;
        double panDegrees = ((currentPanValue * 1.0) / 65535) * 360;

        var currentTiltValue = tiltValue.getValue().intValue();
        int tiltCoarse = currentTiltValue / 256;
        int tiltFine = currentTiltValue % 256;
        double tiltDegrees = 180 - ((currentTiltValue * 1.0) / 65535) * 180;

        valueLabel.setText(String.format("Pan: %.0f°, Tilt: %.0f°, Speed: %.0f",
                panDegrees, tiltDegrees, speedSlider.getValue()));

        clients.stream()
                .filter(c -> c.hasChannel("pan"))
                .forEach(c -> c.setValue("pan", (byte) panCoarse));
        clients.stream()
                .filter(c -> c.hasChannel("pan fine"))
                .forEach(c -> c.setValue("pan fine", (byte) panFine));
        clients.stream()
                .filter(c -> c.hasChannel("tilt"))
                .forEach(c -> c.setValue("tilt", (byte) tiltCoarse));
        clients.stream()
                .filter(c -> c.hasChannel("tilt fine"))
                .forEach(c -> c.setValue("tilt fine", (byte) tiltFine));
        clients.stream()
                .filter(c -> c.hasChannel("Pan/Tilt Speed"))
                .forEach(c -> c.setValue("Pan/Tilt Speed", (byte) speedSlider.getValue()));

        controller.render(clients);
    }
}