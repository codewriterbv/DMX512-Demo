package be.codewriter.dmx512demo.client;

import be.codewriter.dmx512.client.DMXClient;
import be.codewriter.dmx512.controller.DMXController;
import be.codewriter.dmx512demo.client.data.ListItem;
import javafx.geometry.VPos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class DMXControllers extends FlowPane {
    public DMXControllers(DMXController controller, List<DMXClient> clients) {
        setRowValignment(VPos.TOP);
        setHgap(10); // horizontal gap between elements
        setVgap(10); // vertical gap between rows

        getChildren().add(new RGBController(controller, clients));
        getChildren().add(getColorWheel(controller, clients));
        getChildren().add(getGoboWheel(controller, clients));
        getChildren().add(new PanTiltController(controller, clients));
        getChildren().add(new SingleSliderController(controller, clients, "Dimmer", 255));
        getChildren().add(new SingleSliderController(controller, clients, "Shutter / Strobe", 0));
        getChildren().add(getPicoSpotProgram(controller, clients));
        getChildren().add(new SingleSliderController(controller, clients, "Program Speed", 0));
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
    private ListController getColorWheel(DMXController controller, List<DMXClient> clients) {
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
        return new ListController(controller, clients, "Color Wheel", items);
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
    private ListController getGoboWheel(DMXController controller, List<DMXClient> clients) {
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
        return new ListController(controller, clients, "Gobo Wheel", items);
    }

    /**
     * 0 ... 49: NoFunction
     * 50, 59: ColorPreset - White
     * 60, 69: Effect" - Scene 02 (empty)
     * 70, 79: Effect - Scene 03 (empty)
     * 80, 89: Effect - Scene 04 (empty)
     * 90, 99: Effect - Scene 05 (empty)
     * 100, 109: Effect - Scene 06 (empty)
     * 110, 119: Effect - Scene 07 (empty)
     * 120, 129: Effect - Scene 08 (empty)
     * 130, 139: Effect - Scene 09 (empty)
     * 140, 149: Effect - Programme 1
     * 150, 159: Effect - Programme 2
     * 160, 169: Effect - Programme 3
     * 170, 179: Effect - Programme 4
     * 180, 189: Effect - Programme 5
     * 190, 199: Effect - Programme 6
     * 200, 209: Effect - Programme 7
     * 210, 219: Effect - Programme 8 (empty)
     * 220, 229: Effect - Programme 9 (empty)
     * 230, 239: NoFunction - Empty
     * 240, 249: NoFunction - Empty
     * 250, 255: Effect - Sound-controlled operation
     */
    private ListController getPicoSpotProgram(DMXController controller, List<DMXClient> clients) {
        var items = List.of(
                new ListItem((byte) 0, "None", getColorBox(Color.GREY)),
                new ListItem((byte) 50, "White", getColorBox(Color.WHITE)),
                new ListItem((byte) 140, "Program 1", getColorBox(Color.BLACK)),
                new ListItem((byte) 150, "Program 2", getColorBox(Color.BLACK)),
                new ListItem((byte) 160, "Program 3", getColorBox(Color.BLACK)),
                new ListItem((byte) 170, "Program 4", getColorBox(Color.BLACK)),
                new ListItem((byte) 180, "Program 5", getColorBox(Color.BLACK)),
                new ListItem((byte) 190, "Program 6", getColorBox(Color.BLACK)),
                new ListItem((byte) 200, "Program 7", getColorBox(Color.BLACK)),
                new ListItem((byte) 250, "Sound", getColorBox(Color.WHITE))
        );
        return new ListController(controller, clients, "Program", items);
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