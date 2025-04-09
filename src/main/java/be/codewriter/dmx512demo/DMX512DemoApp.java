package be.codewriter.dmx512demo;

import be.codewriter.dmx512.Main;
import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXIPController;
import be.codewriter.dmx512.controller.DMXSerialController;
import be.codewriter.dmx512.ofl.OpenFormatLibraryParser;
import be.codewriter.dmx512.ofl.model.Fixture;
import be.codewriter.dmx512.serial.SerialConnection;
import be.codewriter.dmx512demo.client.DMXClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Comparator;

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

        var serialConnections = new ComboBox<SerialConnection>();
        serialConnections.getItems().addAll(dmxSerialController.getAvailablePorts());
        serialConnections.getItems().sort(Comparator.comparing(SerialConnection::description));
        serialConnections.setConverter(new StringConverter<>() {
            @Override
            public String toString(SerialConnection serialConnection) {
                return serialConnection != null ? serialConnection.description() : ""; // Show name field
            }

            @Override
            public SerialConnection fromString(String string) {
                return dmxSerialController.getAvailablePorts()
                        .stream()
                        .filter(p -> p.description().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        var ledPartyTclSpot = getFixture(FixtureFile.LED_PARTY_TCL_SPOT);
        var picoSpot20Led = getFixture(FixtureFile.PICOSPOT_20_LED);

        var holder = new HBox(new Label("Test"), serialConnections);
        holder.setPadding(new Insets(10));
        holder.setSpacing(10);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(new DMXClientController(new DMXClient(ledPartyTclSpot, ledPartyTclSpot.modes().get(0), 0)));
        tabPane.getTabs().add(new DMXClientController(new DMXClient(ledPartyTclSpot, ledPartyTclSpot.modes().get(0), 5)));
        tabPane.getTabs().add(new DMXClientController(new DMXClient(picoSpot20Led, picoSpot20Led.getMode("11-channel"), 10)));
        tabPane.getTabs().add(new DMXClientController(new DMXClient(picoSpot20Led, picoSpot20Led.getMode("11-channel"), 12)));
        holder.getChildren().add(tabPane);
        
        var scene = new Scene(holder, 1200, 850);
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

    private enum FixtureFile {
        LED_PARTY_TCL_SPOT("led-party-tcl-spot.json"),
        PICOSPOT_20_LED("picospot-20-led.json");

        private final String fileName;

        FixtureFile(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
