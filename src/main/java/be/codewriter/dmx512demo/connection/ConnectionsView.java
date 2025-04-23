package be.codewriter.dmx512demo.connection;

import be.codewriter.dmx512.controller.DMXIPController;
import be.codewriter.dmx512.controller.DMXSerialController;
import be.codewriter.dmx512.network.DMXIpDevice;
import be.codewriter.dmx512.serial.SerialConnection;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.Comparator;

public class ConnectionsView extends VBox {

    public ConnectionsView(DMXSerialController dmxSerialController, DMXIPController dmxIpController) {
        var serialConnections = new ComboBox<SerialConnection>();
        serialConnections.getItems().addAll(dmxSerialController.getAvailablePorts());
        serialConnections.getItems().sort(Comparator.comparing(SerialConnection::description));
        serialConnections.setConverter(new StringConverter<>() {
            @Override
            public String toString(SerialConnection serialConnection) {
                return serialConnection != null ? serialConnection.description() : "";
            }

            @Override
            public SerialConnection fromString(String string) {
                // Not needed
                return null;
            }
        });
        serialConnections.setOnAction(_ -> {
            var selectedPort = serialConnections.getValue();
            if (selectedPort != null) {
                dmxSerialController.connect(selectedPort.name());
            }
        });
        serialConnections.setMaxWidth(Double.MAX_VALUE);

        var serialConnected = new OnOffIndicator("Serial connected", dmxSerialController);

        var ipConnections = new ComboBox<DMXIpDevice>();
        ipConnections.getItems().addAll(dmxIpController.discoverDevices());
        ipConnections.getItems().sort(Comparator.comparing(DMXIpDevice::name));
        ipConnections.setConverter(new StringConverter<>() {
            @Override
            public String toString(DMXIpDevice dmxIpDevice) {
                return dmxIpDevice != null ? dmxIpDevice.name() : "";
            }

            @Override
            public DMXIpDevice fromString(String string) {
                // Not needed
                return null;
            }
        });
        ipConnections.setCellFactory(p -> new ListCell<>() {
            @Override
            protected void updateItem(DMXIpDevice item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    // Format with both name and ID for dropdown items
                    setText(item.name()
                            + " (" + item.ipAddress() + ")\n"
                            + "Protocol: " + item.protocol()
                            + ", universes: " + item.universeCount());
                }
            }
        });
        ipConnections.setOnAction(_ -> {
            var selectedDevice = ipConnections.getValue();
            if (selectedDevice != null) {
                dmxIpController.connect(selectedDevice.ipAddress());
            }
        });
        ipConnections.setMaxWidth(Double.MAX_VALUE);

        var ipConnected = new OnOffIndicator("IP connected", dmxIpController);

        this.getChildren().addAll(new Label("Connections"), serialConnections, serialConnected, ipConnections, ipConnected);
        this.setPrefWidth(250);
        this.setPadding(new Insets(10));
        this.setSpacing(10);
    }
}
