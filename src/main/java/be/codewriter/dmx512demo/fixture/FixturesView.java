package be.codewriter.dmx512demo.fixture;

import be.codewriter.dmx512.controller.DMXController;
import be.codewriter.dmx512.model.DMXUniverse;
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

    private final DMXController controller;
    private final DMXUniverse universe;

    public FixturesView(DMXController controller, List<Fixture> fixtures, DMXUniverse universe) {
        this.controller = controller;
        this.universe = universe;

        fixtures.forEach(f -> {
            var fixtureClients = universe.getFixtureClients(f);
            this.getPanes().add(getFixtureView(f));
        });
        this.setExpandedPane(this.getPanes().getFirst());
    }

    private TitledPane getFixtureView(Fixture fixture) {
        var pane = new TitledPane();
        pane.setText(fixture.name());

        var holder = new HBox();
        holder.setSpacing(10);
        pane.setContent(holder);

        var info = new FixtureInfo(fixture, universe.getFixtureClients(fixture));
        info.setPrefWidth(300);
        holder.getChildren().add(info);

        if (fixture.name().equalsIgnoreCase("LED PARty TCL Spot")) {
            var controllers = getLedPartyTCLControllers(fixture);
            HBox.setHgrow(controllers, Priority.ALWAYS);
            holder.getChildren().add(controllers);
        } else if (fixture.name().equalsIgnoreCase("PicoSpot 20 LED")) {
            var controllers = new PicoSpotView(controller, universe, fixture);
            HBox.setHgrow(controllers, Priority.ALWAYS);
            holder.getChildren().add(controllers);
        }

        return pane;
    }

    private FlowPane getLedPartyTCLControllers(Fixture fixture) {
        var holder = new FlowPane();
        holder.setRowValignment(VPos.TOP);
        holder.setHgap(10);
        holder.setVgap(10);

        holder.getChildren().add(new RGBController(controller, universe, fixture));
        holder.getChildren().add(new SingleSliderController(controller, universe, fixture, "Dimmer", 255, Orientation.VERTICAL));

        return holder;
    }
}
