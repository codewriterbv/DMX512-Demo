package be.codewriter.dmx512demo;

import be.codewriter.dmx512.Main;
import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXIPController;
import be.codewriter.dmx512.controller.DMXSerialController;
import be.codewriter.dmx512.ofl.OpenFormatLibraryParser;
import be.codewriter.dmx512.ofl.model.Fixture;
import be.codewriter.dmx512demo.client.DMXControllers;
import be.codewriter.dmx512demo.connection.ConnectionsView;
import be.codewriter.dmx512demo.fixture.FixturesView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
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

        // Connections
        holder.getChildren().add(new ConnectionsView(dmxSerialController, dmxIpController));

        // Fixtures
        var ledPartyTclSpot = getFixture(FixtureFile.LED_PARTY_TCL_SPOT);
        var picoSpot20Led = getFixture(FixtureFile.PICOSPOT_20_LED);

        if (ledPartyTclSpot != null && picoSpot20Led != null) {
            holder.getChildren().add(new FixturesView(List.of(ledPartyTclSpot, picoSpot20Led)));

            var ledPartyTclSpot1 = new DMXClient(ledPartyTclSpot, ledPartyTclSpot.modes().getFirst(), 0);
            var ledPartyTclSpot2 = new DMXClient(ledPartyTclSpot, ledPartyTclSpot.modes().getFirst(), 5);
            var picoSpot1 = new DMXClient(picoSpot20Led, picoSpot20Led.getMode("11-channel"), 10);
            var picoSpot2 = new DMXClient(picoSpot20Led, picoSpot20Led.getMode("11-channel"), 22);
            var clients = List.of(ledPartyTclSpot1, ledPartyTclSpot2, picoSpot1, picoSpot2);

            // Controllers
            var controllers = new DMXControllers(dmxSerialController, clients);
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
