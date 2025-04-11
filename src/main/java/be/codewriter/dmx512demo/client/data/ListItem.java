package be.codewriter.dmx512demo.client.data;

import javafx.scene.Node;

public record ListItem(byte value, String description, Node marker) {
}
