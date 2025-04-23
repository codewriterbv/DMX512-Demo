package be.codewriter.dmx512demo.fixture;

import be.codewriter.dmx512.ofl.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FixtureInfo extends TitledPane {
    private static final Logger LOGGER = LogManager.getLogger(FixtureInfo.class.getName());

    private static final int TITLE_FONT_SIZE = 18;
    private static final int TEXT_FONT_SIZE = 14;
    private final GridPane grid;
    private int rowIndex = 0;

    public FixtureInfo(Fixture fixture) {
        this.setText(fixture.name());

        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        this.setContent(grid);

        //addRow(fixture.name(), TITLE_FONT_SIZE);
        //addRow("Channel", String.valueOf(client.getStartChannel()));
        //addRow("Mode", client.getSelectedMode().name());
        if (fixture.categories() != null) {
            addRow(fixture.categories().size() == 1 ? "Category" : "Categories", String.join("\n", fixture.categories()));
        }

        if (fixture.meta() != null) {
            addRow(getMeta(fixture.meta()));
        }
        if (fixture.physical() != null) {
            addRow(getPhysical(fixture.physical()));
        }
        if (fixture.links() != null) {
            addRow(getLinks(fixture.links()));
        }
        addRow(getModes(fixture));
        addRow(getChannels(fixture));
    }

    private TitledPane getModes(Fixture fixture) {
        Accordion accordion = new Accordion();
        accordion.setPadding(new Insets(0, 0, 0, 10));

        // Create a TitledPane for each mode
        for (Mode mode : fixture.modes()) {
            // Create content for this mode's information
            VBox modeContent = new VBox(3);
            modeContent.setPadding(new Insets(5));

            // Add shortName as a label
            Label shortNameLabel = new Label("Short Name: " + mode.shortName());
            shortNameLabel.setStyle("-fx-font-size: 11px;");
            modeContent.getChildren().add(shortNameLabel);

            // Add channels in a nested TitledPane
            if (mode.channels() != null && !mode.channels().isEmpty()) {
                ListView<String> channelsList = new ListView<>();
                var counter = new AtomicInteger(1);
                ObservableList<String> channels = FXCollections.observableArrayList(mode.channels().stream()
                        .map(c -> counter.getAndIncrement() + ". " + c)
                        .toList());
                channelsList.setItems(channels);

                // Set a reasonable size for the channels list
                channelsList.setPrefHeight(Math.min(channels.size() * 24, 100));

                modeContent.getChildren().add(channelsList);
            }

            // Create TitledPane for this mode
            TitledPane modePane = new TitledPane(mode.name(), modeContent);
            modePane.setExpanded(false);

            // Add to main container
            accordion.getPanes().add(modePane);
        }

        // Create main TitledPane
        TitledPane mainPane = new TitledPane("Available Modes (" + fixture.modes().size() + ")", accordion);
        mainPane.setCollapsible(true);
        mainPane.setExpanded(false);

        return mainPane;
    }

    private TitledPane getChannels(Fixture fixture) {
        // Create the root item for the TreeView
        TreeItem<String> rootItem = new TreeItem<>("Channels");
        rootItem.setExpanded(true);

        // Create TreeView
        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(false);

        // Create a TreeItem for each channel
        for (Map.Entry<String, Channel> entry : fixture.availableChannels().entrySet()) {
            String channelName = entry.getKey();
            Channel channel = entry.getValue();

            // Create a TreeItem for this channel
            TreeItem<String> channelItem = new TreeItem<>(channelName);
            rootItem.getChildren().add(channelItem);

            // Add default value if present
            if (channel.defaultValue() != null) {
                TreeItem<String> defaultItem = new TreeItem<>("Default: " + channel.defaultValue());
                channelItem.getChildren().add(defaultItem);
            }

            // Add aliases if present
            if (channel.fineChannelAliases() != null && !channel.fineChannelAliases().isEmpty()) {
                TreeItem<String> aliasesItem = new TreeItem<>("Alias(es): " + String.join(", ", channel.fineChannelAliases()));
                channelItem.getChildren().add(aliasesItem);
            }

            // Add capabilities if present
            if (channel.capabilities() != null && !channel.capabilities().isEmpty()) {
                TreeItem<String> capabilitiesItem = new TreeItem<>("Capabilities");
                channelItem.getChildren().add(capabilitiesItem);

                // Add each capability as a child node
                for (Capability capability : channel.capabilities()) {
                    TreeItem<String> capabilityItem = new TreeItem<>(capability.type().name()
                            + (capability.effectName() != null ? ": " + capability.effectName() : ""));
                    capabilitiesItem.getChildren().add(capabilityItem);

                    // Add capability details
                    if (capability.dmxRange() != null) {
                        capabilityItem.getChildren().add(new TreeItem<>("DMX Range: " + capability.dmxRange()));
                    }
                    if (capability.angleStart() != null) {
                        capabilityItem.getChildren().add(new TreeItem<>("Angle Start: " + capability.angleStart()));
                    }
                    if (capability.angleEnd() != null) {
                        capabilityItem.getChildren().add(new TreeItem<>("Angle End: " + capability.angleEnd()));
                    }
                    if (capability.shutterEffect() != null) {
                        capabilityItem.getChildren().add(new TreeItem<>("Shutter Effect: " + capability.shutterEffect()));
                    }
                    if (capability.slotNumber() != null) {
                        capabilityItem.getChildren().add(new TreeItem<>("Slot Number: " + capability.slotNumber()));
                    }
                    if (capability.slotNumberStart() != null) {
                        capabilityItem.getChildren().add(new TreeItem<>("Slot Number Start: " + capability.slotNumberStart()));
                    }
                    if (capability.slotNumberEnd() != null) {
                        capabilityItem.getChildren().add(new TreeItem<>("Slot Number End: " + capability.slotNumberEnd()));
                    }
                    if (capability.speedStart() != null) {
                        capabilityItem.getChildren().add(new TreeItem<>("Speed Start: " + capability.speedStart()));
                    }
                    if (capability.speedEnd() != null) {
                        capabilityItem.getChildren().add(new TreeItem<>("Speed End: " + capability.speedEnd()));
                    }
                }
            }
        }

        // Set preferred size for the tree view
        treeView.setPrefHeight(400);
        treeView.setPrefWidth(300);

        // Apply some styling to make the tree view more readable
        treeView.setStyle("-fx-font-size: 12px;");

        // Add a cell factory to customize the appearance of tree items
        treeView.setCellFactory(tv -> new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    // Add styling based on the content
                    if (item.startsWith("Capability:")) {
                        setStyle("-fx-font-weight: bold;");
                    } else if (getTreeItem() != null && getTreeItem().getParent() == rootItem) {
                        // Channel names (top level items)
                        setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                    } else {
                        setStyle("-fx-font-weight: normal;");
                    }
                }
            }
        });

        // Add a selection listener to close other nodes when a new one is selected
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Get the parent of the selected item (to find siblings)
                TreeItem<String> parent = newValue.getParent();

                if (parent != null) {
                    // If the selected item is a top-level item (direct child of root)
                    if (parent == rootItem) {
                        // Collapse all top-level items except the selected one
                        for (TreeItem<String> item : rootItem.getChildren()) {
                            if (item != newValue) {
                                item.setExpanded(false);
                            }
                        }
                    }
                    // If the selected item is a second-level item (capabilities, aliases, etc.)
                    else if (parent.getParent() == rootItem) {
                        // Get the "grandparent" which would be the root
                        TreeItem<String> channelItem = parent;

                        // Collapse all other channel items
                        for (TreeItem<String> item : rootItem.getChildren()) {
                            if (item != channelItem) {
                                item.setExpanded(false);
                            }
                        }
                    }
                    // If the selected item is a capability
                    else if (parent.getValue().equals("Capabilities")) {
                        TreeItem<String> channelItem = parent.getParent();

                        // Collapse all sibling capabilities
                        for (TreeItem<String> capabilityItem : parent.getChildren()) {
                            if (capabilityItem != newValue) {
                                capabilityItem.setExpanded(false);
                            }
                        }

                        // Collapse all other channel items
                        for (TreeItem<String> item : rootItem.getChildren()) {
                            if (item != channelItem) {
                                item.setExpanded(false);
                            }
                        }
                    }
                    // If the selected item is a capability detail
                    else {
                        TreeItem<String> capabilityItem = parent;
                        TreeItem<String> capabilitiesItem = capabilityItem.getParent();
                        TreeItem<String> channelItem = capabilitiesItem.getParent();

                        // Collapse all sibling capabilities
                        for (TreeItem<String> item : capabilitiesItem.getChildren()) {
                            if (item != capabilityItem) {
                                item.setExpanded(false);
                            }
                        }

                        // Collapse all other channel items
                        for (TreeItem<String> item : rootItem.getChildren()) {
                            if (item != channelItem) {
                                item.setExpanded(false);
                            }
                        }
                    }
                }

                // Always expand the selected item and its parents
                TreeItem<String> current = newValue;
                while (current != null) {
                    current.setExpanded(true);
                    current = current.getParent();
                }
            }
        });

        // Create the titled pane
        TitledPane mainPane = new TitledPane("Available Channels (" + fixture.availableChannels().size() + ")", treeView);
        mainPane.setCollapsible(true);
        mainPane.setExpanded(true);

        return mainPane;
    }

    private TitledPane getLinks(Links links) {
        VBox content = new VBox(3);
        content.setPadding(new Insets(5));

        if (links.manual() != null) {
            for (var link : links.manual()) {
                content.getChildren().add(getLink("Manual", link));
            }
        }
        if (links.productPage() != null) {
            for (var link : links.productPage()) {
                content.getChildren().add(getLink("Product page", link));
            }
        }
        if (links.video() != null) {
            for (var link : links.video()) {
                content.getChildren().add(getLink("Video", link));
            }
        }

        TitledPane pane = new TitledPane("Links", content);
        pane.setCollapsible(true);
        pane.setExpanded(false);
        return pane;
    }

    private TitledPane getPhysical(Physical physical) {
        VBox content = new VBox(3);
        content.setPadding(new Insets(5));

        if (physical.dimensions() != null) {
            Label lbl = new Label("Dimensions: " + physical.dimensions());
            lbl.setStyle("-fx-font-size: 11px;");
            content.getChildren().add(lbl);
        }
        if (physical.weight() != null) {
            Label lbl = new Label("Weight: " + physical.weight() + "kg");
            lbl.setStyle("-fx-font-size: 11px;");
            content.getChildren().add(lbl);
        }
        if (physical.power() != null) {
            Label lbl = new Label("Power: " + physical.power() + "W");
            lbl.setStyle("-fx-font-size: 11px;");
            content.getChildren().add(lbl);
        }
        if (physical.DMXconnector() != null) {
            Label lbl = new Label("DMX connector: " + physical.DMXconnector());
            lbl.setStyle("-fx-font-size: 11px;");
            content.getChildren().add(lbl);
        }
        if (physical.bulb() != null) {
            Label lbl = new Label("Bulb: " + physical.bulb().type());
            lbl.setStyle("-fx-font-size: 11px;");
            content.getChildren().add(lbl);
        }

        TitledPane pane = new TitledPane("Physical", content);
        pane.setCollapsible(true);
        pane.setExpanded(false);
        return pane;
    }

    private TitledPane getMeta(Meta meta) {
        VBox content = new VBox(3);
        content.setPadding(new Insets(5));

        if (meta.authors() != null) {
            Label lbl = new Label("Authors: " + String.join(", ", meta.authors()));
            lbl.setStyle("-fx-font-size: 11px;");
            content.getChildren().add(lbl);
        }
        if (meta.createDate() != null) {
            Label lbl = new Label("Create date: " + meta.createDate());
            lbl.setStyle("-fx-font-size: 11px;");
            content.getChildren().add(lbl);
        }
        if (meta.lastModifyDate() != null) {
            Label lbl = new Label("Last modified: " + meta.lastModifyDate());
            lbl.setStyle("-fx-font-size: 11px;");
            content.getChildren().add(lbl);
        }

        TitledPane pane = new TitledPane("Meta", content);
        pane.setCollapsible(true);
        pane.setExpanded(false);
        return pane;
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