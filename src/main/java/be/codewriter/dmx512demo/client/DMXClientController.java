package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class DMXClientController extends Tab {

    public DMXClientController(DMXClient client) {
        this.setText("DMXClient " + client.getFixture().name() + " (" + client.getStartChannel() + ")");
        this.setClosable(false);

        BorderPane content = new BorderPane();
        content.setLeft(new DMXClientInfo(client));
        this.setContent(content);
    }
}
