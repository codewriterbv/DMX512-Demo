package be.codewriter.dmx512demo.window;

import be.codewriter.dmx512.controller.serial.DMXSerialDiscoverTool;
import be.codewriter.dmx512.serial.SerialConnection;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SerialDiscoveryWindow {
    private static final Logger LOGGER = LogManager.getLogger(SerialDiscoveryWindow.class.getName());
    private final BorderPane layout;
    private Stage stage;
    private TableView<SerialConnection> table;
    private ProgressIndicator progressIndicator;

    public SerialDiscoveryWindow(Stage parentStage) {
        layout = new BorderPane();
        layout.setPadding(new Insets(10));

        createWindow(parentStage);
    }

    private void createWindow(Stage parentStage) {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        stage.setTitle("Serial Port Discovery");
        stage.setResizable(false);

        // Create table view for serial ports
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Create columns - adjust property names based on actual SerialConnection getters
        TableColumn<SerialConnection, String> columnPortName = new TableColumn<>("Port Name");
        columnPortName.setCellValueFactory(new PropertyValueFactory<>("path"));

        TableColumn<SerialConnection, String> columnName = new TableColumn<>("Name");
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<SerialConnection, String> columnDescription = new TableColumn<>("Description");
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Add columns to table
        table.getColumns().addAll(columnPortName, columnName, columnDescription);

        // Create progress indicator
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        // Stack pane to overlay progress indicator
        StackPane centerPane = new StackPane();
        centerPane.getChildren().addAll(table, progressIndicator);
        layout.setCenter(centerPane);

        // Create buttons
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> discoverDevices());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        buttonBox.getChildren().addAll(refreshButton, closeButton);
        layout.setTop(buttonBox);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setHeight(400);
        stage.setWidth(800);

        // Load initial data
        discoverDevices();
    }

    private void discoverDevices() {
        // Show progress indicator and disable button
        progressIndicator.setVisible(true);
        table.getItems().clear();
        layout.setBottom(new Label("Discovering devices..."));

        // Run discovery in background thread
        var discoveryThread = new Thread(() -> {
            try {
                List<SerialConnection> availablePorts = DMXSerialDiscoverTool.getAvailablePorts();

                // Update UI on JavaFX Application Thread
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    table.getItems().clear();
                    layout.setBottom(null);
                    if (availablePorts.isEmpty()) {
                        layout.setBottom(new Label("No serial ports found"));
                    } else {
                        table.getItems().addAll(availablePorts);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Error discovering serial ports: {}", e.getMessage());
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    table.getItems().clear();
                    layout.setBottom(null);
                    layout.setBottom(new Label("Error discovering ports: " + e.getMessage()));
                });
            }
        });

        discoveryThread.setDaemon(true);
        discoveryThread.start();
    }

    public void show() {
        stage.show();
    }
}