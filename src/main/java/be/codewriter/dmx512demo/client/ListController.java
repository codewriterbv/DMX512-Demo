package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXController;
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
    private final DMXController controller;
    private final List<DMXClient> clients;
    private final ObservableList<ListItem> observableItems;

    public ListController(DMXController controller, List<DMXClient> clients, String key, List<ListItem> items) {
        this.controller = controller;
        this.clients = clients;
        observableItems = FXCollections.observableArrayList(items);
        setItems(observableItems);

        // Set the preferred height based on items
        setPrefHeight(USE_COMPUTED_SIZE);

        // Calculate and set the max height based on number of items
        // Assuming each cell is 20px high (based on your hbox.setPrefHeight(20))
        // Plus 2 pixels for cell borders/padding if any
        setMaxHeight((items.size() * 22) + 10);

        // Disable scrollbars since we're showing all items
        setFixedCellSize(22);

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
        clients.stream()
                .filter(c -> c.hasChannel(key))
                .forEach(c -> c.setValue(key, value));
        controller.render(clients);
    }

    static class ColorItemCell extends ListCell<ListItem> {
        private final HBox hbox;
        private final Pane pane;
        private final Text text;

        public ColorItemCell() {
            super();

            setStyle("-fx-padding: 0; -fx-background-insets: 0; -fx-border-width: 0;");

            hbox = new HBox(10); // 10 is the spacing
            hbox.setPrefHeight(20);
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
