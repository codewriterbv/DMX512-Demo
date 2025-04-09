package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.ofl.model.Links;
import be.codewriter.dmx512.ofl.model.Meta;
import be.codewriter.dmx512.ofl.model.Physical;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DMXClientInfo extends TitledPane {
    private static final Logger LOGGER = LogManager.getLogger(DMXClientInfo.class.getName());

    private static final int TITLE_FONT_SIZE = 18;
    private static final int TEXT_FONT_SIZE = 14;
    private final GridPane grid;
    private int rowIndex = 0;

    public DMXClientInfo(DMXClient client) {
        this.setText("DMXClient " + client.getFixture().name() + " (" + client.getStartChannel() + ")");

        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        this.setContent(grid);

        var fixture = client.getFixture();
        addRow(fixture.name(), TITLE_FONT_SIZE);
        addRow("Mode", client.getSelectedMode().name());
        addRow("Channel", String.valueOf(client.getStartChannel()));
        addRow(" ", " ");

        addRow("Categories", fixture.categories() == null ? "" : String.join(", ", fixture.categories()));
        addRow(" ", " ");

        addRow("Meta", " ", TITLE_FONT_SIZE);
        addMeta(fixture.meta());
        addRow(" ", " ");

        addRow("Fixture", " ", TITLE_FONT_SIZE);
        addPhysical(fixture.physical());
        addRow(" ", " ");

        addLinks(fixture.links());
    }

    private void addLinks(Links links) {
        if (links == null) {
            return;
        }
        var linksBox = new VBox(5);
        linksBox.setPadding(new Insets(0));
        if (links.manual() != null) {
            for (var link : links.manual()) {
                linksBox.getChildren().add(getLink("Manual", link));
            }
        }
        if (links.productPage() != null) {
            for (var link : links.productPage()) {
                linksBox.getChildren().add(getLink("Product page", link));
            }
        }
        if (links.video() != null) {
            for (var link : links.video()) {
                linksBox.getChildren().add(getLink("Video", link));
            }
        }
        if (!linksBox.getChildren().isEmpty()) {
            addRow("Links", linksBox);
        }
    }

    private void addPhysical(Physical physical) {
        if (physical == null) {
            return;
        }
        addRow("Dimensions", physical.dimensions().toString());
        addRow("Weight", physical.weight() + " kg");
        addRow("Power", physical.power() + " W");
        addRow("DMXconnector", physical.DMXconnector());
        var bulb = physical.bulb();
        if (bulb != null) {
            addRow("Bulb", bulb.type());
        }
    }

    private void addMeta(Meta meta) {
        if (meta == null) {
            return;
        }
        addRow("Authors", meta.authors() == null ? "" : String.join(", ", meta.authors()));
        addRow("Create date", meta.createDate());
        addRow("Last modify date", meta.lastModifyDate());
    }

    private Hyperlink getLink(String text, String url) {
        var hyperlink = new Hyperlink(text);
        hyperlink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException ex) {
                LOGGER.error("Error opening link: {}", ex.getMessage());
            }
        });
        hyperlink.setStyle("-fx-font-size: " + TEXT_FONT_SIZE + "px; -fx-font-weight: bold; -fx-margin: 0; -fx-padding: 0;");
        return hyperlink;
    }

    private void addRow(String label, int fontSize) {
        var lbl = getLabel(label, fontSize, false);
        grid.add(lbl, 0, rowIndex, 2, 1);
        GridPane.setValignment(lbl, VPos.TOP);
        GridPane.setValignment(lbl, VPos.TOP);

        rowIndex++;
    }

    private void addRow(String label, String value) {
        addRow(getLabel(label, TEXT_FONT_SIZE, false), getLabel(value, TEXT_FONT_SIZE, true));
    }

    private void addRow(String label, String value, int fontSize) {
        addRow(getLabel(label, fontSize, false), getLabel(value, fontSize, true));
    }

    private void addRow(String label, Node node) {
        addRow(getLabel(label, TEXT_FONT_SIZE, false), node);
    }

    private void addRow(Node column1, Node column2) {
        grid.add(column1, 0, rowIndex);
        grid.add(column2, 1, rowIndex);
        GridPane.setValignment(column1, VPos.TOP);
        GridPane.setValignment(column2, VPos.TOP);
        GridPane.setHalignment(column1, HPos.LEFT);
        GridPane.setHalignment(column2, HPos.LEFT);
        rowIndex++;
    }

    private Label getLabel(String text, int fontSize, boolean bold) {
        var label = new Label(text);
        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: " + (bold ? "bold" : "normal") + ";");
        return label;
    }
}