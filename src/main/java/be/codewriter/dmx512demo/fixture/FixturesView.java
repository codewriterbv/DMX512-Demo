package be.codewriter.dmx512demo.fixture;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.ofl.model.Fixture;
import javafx.scene.control.Accordion;

import java.util.List;

public class FixturesView extends Accordion {

    public FixturesView(List<Fixture> fixtures, List<DMXClient> clients) {
        this.setPrefWidth(300);
        fixtures.forEach(f -> this.getPanes().add(new FixtureInfo(f, clients.stream()
                .filter(c -> c.getFixture() == f)
                .toList())));
        this.setExpandedPane(this.getPanes().getFirst());
    }
}
