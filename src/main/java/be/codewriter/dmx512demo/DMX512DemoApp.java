package be.codewriter.dmx512demo;

import be.codewriter.dmx512.Main;
import be.codewriter.dmx512.controller.ip.DMXIPController;
import be.codewriter.dmx512.model.DMXClient;
import be.codewriter.dmx512.model.DMXUniverse;
import be.codewriter.dmx512.ofl.OFLParser;
import be.codewriter.dmx512.ofl.model.Fixture;
import be.codewriter.dmx512demo.connection.ConnectionMonitor;
import be.codewriter.dmx512demo.fixture.FixturesView;
import be.codewriter.dmx512demo.window.AboutWindow;
import be.codewriter.dmx512demo.window.IPDiscoveryWindow;
import be.codewriter.dmx512demo.window.SerialDiscoveryWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class DMX512DemoApp extends Application {
    private static final Logger LOGGER = LogManager.getLogger(DMX512DemoApp.class.getName());
    private BorderPane holder;

    private static Fixture getFixture(FixtureFile fixtureFile) {
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(fixtureFile.getFileName())) {
            return OFLParser.parse(is);
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
    public void start(Stage stage) throws UnknownHostException {
        holder = new BorderPane();
        holder.setPadding(new Insets(10));
        holder.setTop(getMenuBar(stage));

        var controller = new DMXIPController(InetAddress.getByName("172.16.1.144"));
        holder.setBottom(new ConnectionMonitor(controller));

        var ledPartyTclSpot = getFixture(FixtureFile.LED_PARTY_TCL_SPOT);
        var picoSpot20Led = getFixture(FixtureFile.PICOSPOT_20_LED);

        if (ledPartyTclSpot != null && picoSpot20Led != null) {
            // This fixture has three modes, so we need to specify which one we want to use
            var picoSpot1 = new DMXClient(1, picoSpot20Led, picoSpot20Led.getModeByName("11-channel"));
            var picoSpot2 = new DMXClient(12, picoSpot20Led, picoSpot20Led.getModeByName("11-channel"));
            // This fixture only has one mode, so we don't need to provide it
            var ledPartyTclSpot1 = new DMXClient(23, ledPartyTclSpot);
            var ledPartyTclSpot2 = new DMXClient(28, ledPartyTclSpot);

            var universe = new DMXUniverse(1, List.of(ledPartyTclSpot1, ledPartyTclSpot2, picoSpot1, picoSpot2));

            holder.setCenter(new FixturesView(controller, List.of(ledPartyTclSpot, picoSpot20Led), universe));
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

    private MenuBar getMenuBar(Stage stage) {
        var menuBar = new MenuBar();

        // Create Detect Serial menu item
        MenuItem detectSerialItem = new MenuItem("Detect Serial");
        detectSerialItem.setOnAction(e -> showSerialDiscoveryWindow(stage));

        // Create Detect IP menu item
        MenuItem detectIPItem = new MenuItem("Detect IP");
        detectIPItem.setOnAction(e -> showIPDiscoveryWindow(stage));

        // Create Devices menu
        Menu devicesMenu = new Menu("Devices");
        devicesMenu.getItems().addAll(detectSerialItem, detectIPItem);

        // Create About menu item
        MenuItem aboutItem = new MenuItem("About...");
        aboutItem.setOnAction(e -> showAbout(stage));

        // Create Help menu
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(devicesMenu, helpMenu);

        return menuBar;
    }

    private void showSerialDiscoveryWindow(Stage stage) {
        var window = new SerialDiscoveryWindow(stage);
        window.show();
    }

    private void showIPDiscoveryWindow(Stage stage) {
        var window = new IPDiscoveryWindow(stage);
        window.show();
    }

    private void showAbout(Stage stage) {
        var window = new AboutWindow(stage);
        window.show();
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
