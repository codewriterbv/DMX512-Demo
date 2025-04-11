package be.codewriter.dmx512demo;

import be.codewriter.dmx512.Main;
import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXIPController;
import be.codewriter.dmx512.controller.DMXSerialController;
import be.codewriter.dmx512.network.DMXIpDevice;
import be.codewriter.dmx512.ofl.OpenFormatLibraryParser;
import be.codewriter.dmx512.ofl.model.Fixture;
import be.codewriter.dmx512.serial.SerialConnection;
import be.codewriter.dmx512demo.client.DMXClientInfo;
import be.codewriter.dmx512demo.client.DMXControllers;
import be.codewriter.dmx512demo.component.OnOffIndicator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

public class DMX512DemoApp extends Application {
    private static final Logger LOGGER = LogManager.getLogger(DMX512DemoApp.class.getName());

    private static Fixture getFixture(FixtureFile fixtureFile) {
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(fixtureFile.getFileName())) {
            return OpenFormatLibraryParser.parseFixture(is);
        } catch (Exception ex) {
            LOGGER.error("Error parsing fixture: {}", ex.getMessage());
        }

        return null;
    }

    public void run() {
        LOGGER.info("Starting UI...");
        launch();
    }

    @Override
    public void start(Stage stage) {
        var dmxSerialController = new DMXSerialController();
        var dmxIpController = new DMXIPController();

        var holder = new HBox();
        holder.setPadding(new Insets(10));
        holder.setSpacing(10);

        holder.getChildren().add(getDmxConnections(dmxSerialController, dmxIpController));

        var ledPartyTclSpot = getFixture(FixtureFile.LED_PARTY_TCL_SPOT);
        var picoSpot20Led = getFixture(FixtureFile.PICOSPOT_20_LED);

        if (ledPartyTclSpot != null && picoSpot20Led != null) {
            var ledPartyTclSpot1 = new DMXClient(ledPartyTclSpot, ledPartyTclSpot.modes().getFirst(), 0);
            var ledPartyTclSpot2 = new DMXClient(ledPartyTclSpot, ledPartyTclSpot.modes().getFirst(), 5);
            var picoSpot1 = new DMXClient(picoSpot20Led, picoSpot20Led.getMode("9-channel"), 10);
            var picoSpot2 = new DMXClient(picoSpot20Led, picoSpot20Led.getMode("9-channel"), 22);

            Accordion clients = new Accordion();
            clients.setPrefWidth(250);
            clients.getPanes().add(new DMXClientInfo(ledPartyTclSpot1));
            clients.getPanes().add(new DMXClientInfo(ledPartyTclSpot2));
            clients.getPanes().add(new DMXClientInfo(picoSpot1));
            clients.getPanes().add(new DMXClientInfo(picoSpot2));
            clients.setExpandedPane(clients.getPanes().getFirst());

            holder.getChildren().add(clients);

            var controllers = new DMXControllers(dmxSerialController, List.of(ledPartyTclSpot1, ledPartyTclSpot2, picoSpot1, picoSpot2));
            HBox.setHgrow(controllers, Priority.ALWAYS);

            holder.getChildren().add(controllers);
        }

        var scene = new Scene(holder, 1400, 850);
        stage.setScene(scene);
        stage.setTitle("DMX512 Demo");
        stage.setX(25);
        stage.setY(25);
        stage.show();

        stage.setOnCloseRequest(event -> {
            LOGGER.info("Closing application...");
            Platform.exit();
            System.exit(0);
        });
    }

    private VBox getDmxConnections(DMXSerialController dmxSerialController, DMXIPController dmxIpController) {
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
                dmxSerialController.connect(selectedPort.path());
            }
        });
        serialConnections.setMaxWidth(Double.MAX_VALUE);

        var serialConnected = new OnOffIndicator("Serial connected");
        serialConnected.isOn.set(dmxSerialController.isConnected());

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
        ipConnections.setOnAction(_ -> {
            var selectedDevice = ipConnections.getValue();
            if (selectedDevice != null) {
                dmxIpController.connect(selectedDevice.ipAddress());
            }
        });
        ipConnections.setMaxWidth(Double.MAX_VALUE);

        var ipConnected = new OnOffIndicator("IP connected");
        ipConnected.isOn.set(dmxIpController.isConnected());

        var holder = new VBox(new Label("Connections"), serialConnections, serialConnected, ipConnections, ipConnected);
        holder.setPrefWidth(250);
        holder.setPadding(new Insets(10));
        holder.setSpacing(10);

        return holder;
    }

    private enum FixtureFile {
        LED_PARTY_TCL_SPOT("ofl/led-party-tcl-spot.json"),
        PICOSPOT_20_LED("ofl/picospot-20-led.json");

        private final String fileName;

        FixtureFile(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
