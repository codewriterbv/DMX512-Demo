package be.codewriter.dmx512demo.fixture;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXController;
import be.codewriter.dmx512.ofl.model.Fixture;
import be.codewriter.dmx512demo.client.RGBController;
import be.codewriter.dmx512demo.client.SingleSliderController;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.List;

public class FixturesView extends Accordion {

    public FixturesView(DMXController controller, List<Fixture> fixtures, List<DMXClient> clients) {
        fixtures.forEach(f -> {
            var fixtureClients = clients.stream()
                    .filter(c -> c.getFixture() == f)
                    .toList();
            this.getPanes().add(getFixtureView(controller, f, fixtureClients));
        });
        this.setExpandedPane(this.getPanes().getFirst());
    }

    private TitledPane getFixtureView(DMXController controller, Fixture fixture, List<DMXClient> clients) {
        var pane = new TitledPane();
        pane.setText(fixture.name());

        var holder = new HBox();
        holder.setSpacing(10);
        pane.setContent(holder);

        var info = new FixtureInfo(fixture, clients);
        info.setPrefWidth(300);
        holder.getChildren().add(info);

        if (fixture.name().equalsIgnoreCase("LED PARty TCL Spot")) {
            var controllers = getLedPartyTCLControllers(controller, clients);
            HBox.setHgrow(controllers, Priority.ALWAYS); // Allow it to grow
            holder.getChildren().add(controllers);
        } else if (fixture.name().equalsIgnoreCase("PicoSpot 20 LED")) {
            var controllers = new PicoSpotView(controller, clients);
            HBox.setHgrow(controllers, Priority.ALWAYS); // Allow it to grow
            holder.getChildren().add(controllers);
        }

        return pane;
    }

    private FlowPane getLedPartyTCLControllers(DMXController controller, List<DMXClient> clients) {
        var holder = new FlowPane();
        holder.setRowValignment(VPos.TOP);
        holder.setHgap(10); // horizontal gap between elements
        holder.setVgap(10); // vertical gap between rows

        holder.getChildren().add(new RGBController(controller, clients));
        holder.getChildren().add(new SingleSliderController(controller, clients, "Dimmer", 255, Orientation.VERTICAL));

        return holder;
    }
}
