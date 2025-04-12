package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.ofl.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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

        addRow(getModes(client));
        addRow(getChannels(client));

        addRow(" ", " ");

        addLinks(fixture.links());
    }

    private TitledPane getModes(DMXClient client) {
        // Create main VBox to hold all mode TitledPanes
        VBox modesContainer = new VBox(5); // 5px spacing between elements
        modesContainer.setPadding(new Insets(5));

        // Create a TitledPane for each mode
        for (Mode mode : client.getFixture().modes()) {
            // Create content for this mode's information
            VBox modeContent = new VBox(3);
            modeContent.setPadding(new Insets(5));

            // Add shortName as a label
            Label shortNameLabel = new Label("Short Name: " + mode.shortName());
            shortNameLabel.setStyle("-fx-font-size: 11px;");
            modeContent.getChildren().add(shortNameLabel);

            // Add channels in a nested TitledPane
            if (mode.channels() != null && !mode.channels().isEmpty()) {
                TitledPane channelsPane = new TitledPane("Channels", null);
                ListView<String> channelsList = new ListView<>();

                ObservableList<String> channels = FXCollections.observableArrayList(mode.channels());
                channelsList.setItems(channels);

                // Set a reasonable size for the channels list
                channelsList.setPrefHeight(Math.min(channels.size() * 24, 100));

                channelsPane.setContent(channelsList);
                channelsPane.setExpanded(false);
                modeContent.getChildren().add(channelsPane);
            }

            // Create TitledPane for this mode
            TitledPane modePane = new TitledPane(mode.name(), modeContent);
            modePane.setExpanded(false);

            // Add to main container
            modesContainer.getChildren().add(modePane);
        }

        // Create scrollable container if there are many modes
        ScrollPane scrollPane = new ScrollPane(modesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        // Create main TitledPane
        TitledPane mainPane = new TitledPane("Available Modes (" + client.getFixture().modes().size() + ")", scrollPane);
        mainPane.setCollapsible(true);
        mainPane.setExpanded(false);

        return mainPane;
    }

    private TitledPane getChannels(DMXClient client) {
        // Create main VBox to hold all channel TitledPanes
        VBox channelsContainer = new VBox(5); // 5px spacing between elements
        channelsContainer.setPadding(new Insets(5));

        // Create a TitledPane for each channel
        for (Map.Entry<String, Channel> entry : client.getFixture().availableChannels().entrySet()) {
            String channelName = entry.getKey();
            Channel channel = entry.getValue();

            // Create content for this channel's information
            VBox channelContent = new VBox(3);
            channelContent.setPadding(new Insets(5));

            // Add default value if present
            if (channel.defaultValue() != null) {
                Label defaultLabel = new Label("Default: " + channel.defaultValue());
                defaultLabel.setStyle("-fx-font-size: 11px;");
                channelContent.getChildren().add(defaultLabel);
            }

            // Add aliases if present
            if (channel.fineChannelAliases() != null && !channel.fineChannelAliases().isEmpty()) {
                Label aliasesLabel = new Label("Alias(es): " + String.join(", ", channel.fineChannelAliases()));
                aliasesLabel.setStyle("-fx-font-size: 11px;");
                channelContent.getChildren().add(aliasesLabel);
            }

            // Add capabilities if present
            if (channel.capabilities() != null && !channel.capabilities().isEmpty()) {
                TitledPane capabilitiesPane = new TitledPane("Capabilities", null);
                ListView<String> capabilitiesList = new ListView<>();

                ObservableList<String> capabilities = FXCollections.observableArrayList();
                channel.capabilities().forEach(c -> capabilities.add(c.type().name()));
                capabilitiesList.setItems(capabilities);

                // Set a reasonable size for the capabilities list
                capabilitiesList.setPrefHeight(Math.min(capabilities.size() * 24, 100));

                capabilitiesPane.setContent(capabilitiesList);
                capabilitiesPane.setExpanded(false);
                channelContent.getChildren().add(capabilitiesPane);
            }

            // Create TitledPane for this channel
            TitledPane channelPane = new TitledPane(channelName, channelContent);
            channelPane.setExpanded(false);

            // Add to main container
            channelsContainer.getChildren().add(channelPane);
        }

        // Create scrollable container if there are many channels
        ScrollPane scrollPane = new ScrollPane(channelsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        // Create main TitledPane
        TitledPane mainPane = new TitledPane("Available Channels (" + client.getFixture().availableChannels().size() + ")", scrollPane);
        mainPane.setCollapsible(true);
        mainPane.setExpanded(false);

        return mainPane;
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

    private void addRow(Node columnSpan) {
        grid.add(columnSpan, 0, rowIndex, 2, 1);
        GridPane.setValignment(columnSpan, VPos.TOP);
        GridPane.setHalignment(columnSpan, HPos.LEFT);
        rowIndex++;
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