package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXController;
import be.codewriter.dmx512demo.client.data.ListItem;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DMXControllers extends GridPane {
    private static final Logger LOGGER = LogManager.getLogger(DMXControllers.class.getName());

    public DMXControllers(DMXController controller, List<DMXClient> clients) {
        this.setPadding(new Insets(15, 0, 0, 0));

        add(new RGBController(controller, clients), 0, 0);
        add(getColorWheel(controller, clients, "Color Wheel"), 1, 0);
        add(getGoboWheel(controller, clients, "Gobo Wheel"), 2, 0);
    }

    /**
     * 0 	…	10: White
     * 11 	…	21: Red
     * 22 	…	32: Orange
     * 33 	…	43: Yellow
     * 44 	…	54: Green
     * 55 	…	65: Blue
     * 66 	…	76: Cyan
     * 77 	…	87: Purple
     * 88 	…	98: White … Red
     * 99 	…	109: Red … Orange
     * 110 	…	120: Orange … Yellow
     * 121 	…	131: Yellow … Green
     * 132 	…	142: Green … Blue
     * 143 	…	153: Blue … Cyan
     * 154 	…	164: Cyan … Purple
     * 165 	…	175: Purple … White
     * 176 	…	255: Color Wheel rotation CW slow…
     */
    private ListController getColorWheel(DMXController controller, List<DMXClient> clients, String key) {
        var items = List.of(
                new ListItem((byte) 0, "White", getColorBox(Color.WHITE)),
                new ListItem((byte) 11, "Red", getColorBox(Color.RED)),
                new ListItem((byte) 22, "Orange", getColorBox(Color.ORANGE)),
                new ListItem((byte) 33, "Yellow", getColorBox(Color.YELLOW)),
                new ListItem((byte) 44, "Green", getColorBox(Color.GREEN)),
                new ListItem((byte) 55, "Blue", getColorBox(Color.BLUE)),
                new ListItem((byte) 66, "Cyan", getColorBox(Color.CYAN)),
                new ListItem((byte) 77, "Purple", getColorBox(Color.PURPLE)),
                new ListItem((byte) 88, "White-Red", getColorBox(Color.WHITE, Color.RED)),
                new ListItem((byte) 99, "Red-Orange", getColorBox(Color.RED, Color.ORANGE)),
                new ListItem((byte) 110, "Orange-Yellow", getColorBox(Color.ORANGE, Color.YELLOW)),
                new ListItem((byte) 121, "Yellow-Green", getColorBox(Color.YELLOW, Color.GREEN)),
                new ListItem((byte) 132, "Green-Blue", getColorBox(Color.GREEN, Color.BLUE)),
                new ListItem((byte) 143, "Blue-Cyan", getColorBox(Color.BLUE, Color.CYAN)),
                new ListItem((byte) 154, "Cyan-Purple", getColorBox(Color.CYAN, Color.PURPLE)),
                new ListItem((byte) 165, "Purple-White", getColorBox(Color.PURPLE, Color.WHITE))

        );
        return new ListController(controller, clients, key, items);
    }

    /**
     * 0 	…	15: Open
     * 16 	…	31: Gobo 1
     * 32 	…	46: Gobo 2
     * 47 	…	62: Gobo 3
     * 63 	…	78: Gobo 4
     * 79 	…	93: Gobo 5
     * 94 	…	109: Gobo 6
     * 110 	…	124: Gobo 7
     * 125 	…	140: Gobo 1 shake slow…fast
     * 141 	…	156: Gobo 2 shake slow…fast
     * 157 	…	171: Gobo 3 shake slow…fast
     * 172 	…	187: Gobo 4 shake slow…fast
     * 188 	…	203: Gobo 5 shake slow…fast
     * 204 	…	218: Gobo 6 shake slow…fast
     * 219 	…	249: Gobo 7 shake slow…fast
     * 250 	…	255: Gobo Wheel rotation CW slow…fast
     */
    private ListController getGoboWheel(DMXController controller, List<DMXClient> clients, String key) {
        var items = List.of(
                new ListItem((byte) 0, "Open", getColorBox(Color.WHITE)),
                new ListItem((byte) 16, "Gobo 1", getColorBox(Color.WHITE)),
                new ListItem((byte) 32, "Gobo 2", getColorBox(Color.WHITE)),
                new ListItem((byte) 47, "Gobo 3", getColorBox(Color.WHITE)),
                new ListItem((byte) 63, "Gobo 4", getColorBox(Color.WHITE)),
                new ListItem((byte) 79, "Gobo 5", getColorBox(Color.WHITE)),
                new ListItem((byte) 94, "Gobo 6", getColorBox(Color.WHITE)),
                new ListItem((byte) 110, "Gobo 7", getColorBox(Color.WHITE))

        );
        return new ListController(controller, clients, key, items);
    }

    private Pane getColorBox(Color color) {
        return getColorBox(color, color);
    }

    private Pane getColorBox(Color color1, Color color2) {
        // Create container for the split colored box
        Pane colorBox = new Pane();
        colorBox.setPrefSize(30, 30);
        colorBox.setMinSize(30, 30);

        // Set up rectangles for split color box
        var leftHalf = new Rectangle(0, 0, 15, 30);
        leftHalf.setFill(color1);
        var rightHalf = new Rectangle(15, 0, 15, 30);
        rightHalf.setFill(color2);

        colorBox.getChildren().addAll(leftHalf, rightHalf);

        return colorBox;
    }
}