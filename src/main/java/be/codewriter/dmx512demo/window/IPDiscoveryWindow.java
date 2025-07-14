package be.codewriter.dmx512demo.window;

import be.codewriter.dmx512.controller.ip.DMXIPDiscoverTool;
import be.codewriter.dmx512.network.DMXIPDevice;
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

public class IPDiscoveryWindow {
    private static final Logger LOGGER = LogManager.getLogger(IPDiscoveryWindow.class.getName());
    private final BorderPane layout;
    private Stage stage;
    private TableView<DMXIPDevice> table;
    private ProgressIndicator progressIndicator;

    public IPDiscoveryWindow(Stage parentStage) {
        layout = new BorderPane();
        layout.setPadding(new Insets(10));

        createWindow(parentStage);
    }

    private void createWindow(Stage parentStage) {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        stage.setTitle("IP Device Discovery");
        stage.setResizable(false);

        // Create list view for devices
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Create columns - adjust property names based on actual DMXIPDevice getters
        TableColumn<DMXIPDevice, String> columnAddress = new TableColumn<>("Address");
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<DMXIPDevice, String> columnName = new TableColumn<>("Name");
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<DMXIPDevice, String> columnProtocol = new TableColumn<>("Protocol");
        columnProtocol.setCellValueFactory(new PropertyValueFactory<>("protocol"));

        TableColumn<DMXIPDevice, String> columnUniverseCount = new TableColumn<>("Universe count");
        columnUniverseCount.setCellValueFactory(new PropertyValueFactory<>("universeCount"));

        // Add columns to table
        table.getColumns().addAll(columnAddress, columnProtocol, columnName, columnUniverseCount);

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
        Thread discoveryThread = new Thread(() -> {
            try {
                var discoveredDevices = DMXIPDiscoverTool.discoverDevices();

                // Update UI on JavaFX Application Thread
                Platform.runLater(() -> {
                    table.getItems().clear();
                    layout.setBottom(null);
                    if (discoveredDevices.isEmpty()) {
                        layout.setBottom(new Label("No IP devices found"));
                    } else {
                        table.getItems().addAll(discoveredDevices);
                    }
                    progressIndicator.setVisible(false);
                });
            } catch (Exception e) {
                LOGGER.error("Error discovering IP devices: {}", e.getMessage());
                Platform.runLater(() -> {
                    table.getItems().clear();
                    layout.setBottom(null);
                    layout.setBottom(new Label("Error discovering devices: " + e.getMessage()));
                    progressIndicator.setVisible(false);
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