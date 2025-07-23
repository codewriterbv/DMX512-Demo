package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.controller.DMXController;
import be.codewriter.dmx512.model.DMXUniverse;
import be.codewriter.dmx512.ofl.model.Fixture;
import be.codewriter.dmx512demo.client.data.ListItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import java.util.List;

public class ListController extends ListView<ListItem> {
    public static final int LIST_ITEM_HEIGHT = 25;
    private final DMXController controller;
    private final DMXUniverse universe;
    private final Fixture fixture;
    private final ObservableList<ListItem> observableItems;

    public ListController(DMXController controller, DMXUniverse universe, Fixture fixture, String key, List<ListItem> items) {
        this.controller = controller;
        this.universe = universe;
        this.fixture = fixture;

        observableItems = FXCollections.observableArrayList(items);
        setItems(observableItems);

        // Set the preferred height based on items
        var neededHeight = (items.size() * LIST_ITEM_HEIGHT) + 5;
        setPrefHeight(neededHeight);
        setMaxHeight(neededHeight);

        // Disable scrollbars since we're showing all items
        setFixedCellSize(LIST_ITEM_HEIGHT);

        setCellFactory(_ -> new ColorItemCell());

        setOnMouseClicked(event -> {
            ListItem selectedItem = getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                updateClients(key, selectedItem.value());
            }
        });

        // Select first item and update clients
        if (!items.isEmpty()) {
            getSelectionModel().select(0);
            updateClients(key, items.getFirst().value());
        }
    }

    private void updateClients(String key, byte value) {
        universe.updateFixtures(fixture, key, value);
        controller.render(universe);
    }

    static class ColorItemCell extends ListCell<ListItem> {
        private final HBox hbox;
        private final Pane pane;
        private final Text text;

        public ColorItemCell() {
            super();

            setStyle("-fx-padding: 0; -fx-background-insets: 0; -fx-border-width: 0;");

            hbox = new HBox(10); // 10 is the spacing
            hbox.setPrefHeight(LIST_ITEM_HEIGHT);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setPadding(new Insets(0));

            pane = new Pane();
            text = new Text();

            // Add spacer pane that grows to push content left
            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            hbox.getChildren().addAll(pane, text, spacer);
        }

        @Override
        protected void updateItem(ListItem item, boolean empty) {
            super.updateItem(item, empty);

            setText(null); // Clear default text

            if (empty || item == null) {
                setGraphic(null);
            } else {
                // Update the rectangles with the item's colors
                pane.getChildren().clear();
                pane.getChildren().add(item.marker());

                // Set the description text
                text.setText(item.description());

                // Set the custom cell graphic
                setGraphic(hbox);
            }
        }
    }
}
