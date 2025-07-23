package be.codewriter.dmx512demo.fixture;

import be.codewriter.dmx512.controller.DMXController;
import be.codewriter.dmx512.model.DMXUniverse;
import be.codewriter.dmx512.ofl.model.Fixture;
import be.codewriter.dmx512demo.client.ListController;
import be.codewriter.dmx512demo.client.PanTiltController;
import be.codewriter.dmx512demo.client.SingleSliderController;
import be.codewriter.dmx512demo.client.data.ListItem;
import be.codewriter.dmx512demo.helper.ImageHelper;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

import static be.codewriter.dmx512demo.client.ListController.LIST_ITEM_HEIGHT;

public class PicoSpotView extends FlowPane {
    private final DMXController controller;
    private final DMXUniverse universe;
    private final Fixture fixture;

    public PicoSpotView(DMXController controller, DMXUniverse universe, Fixture fixture) {
        this.controller = controller;
        this.universe = universe;
        this.fixture = fixture;

        setRowValignment(VPos.TOP);
        setHgap(10); // horizontal gap between elements
        setVgap(10); // vertical gap between rows

        var programList = getPicoSpotProgram();
        var colorList = getColorWheel();
        var goboList = getGoboWheel();

        // Add listener to program list to enable/disable color and gobo lists
        programList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            boolean isFirstItemSelected = newValue.intValue() == 0;
            colorList.setDisable(!isFirstItemSelected);
            goboList.setDisable(!isFirstItemSelected);
        });

        // Initially set the state based on the default selection (first item)
        boolean isFirstItemSelected = programList.getSelectionModel().getSelectedIndex() == 0;
        colorList.setDisable(!isFirstItemSelected);
        goboList.setDisable(!isFirstItemSelected);

        getChildren().add(getProgramView(programList));
        getChildren().add(colorList);
        getChildren().add(goboList);
        getChildren().add(new PanTiltController(controller, universe, fixture));
        getChildren().add(new SingleSliderController(controller, universe, fixture, "Dimmer", 255, Orientation.VERTICAL));
        getChildren().add(new SingleSliderController(controller, universe, fixture, "Shutter / Strobe", 0, Orientation.VERTICAL));
    }

    private VBox getProgramView(ListController programList) {
        var holder = new VBox();
        holder.setSpacing(10);
        holder.getChildren().addAll(programList,
                new SingleSliderController(controller, universe, fixture, "Program Speed", 127, Orientation.HORIZONTAL));
        return holder;
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
    private ListController getColorWheel() {
        var items = List.of(
                new ListItem((byte) 0, "White", getColorBox(Color.WHITE)),
                new ListItem((byte) 11, "Red", getColorBox(Color.RED)),
                new ListItem((byte) 22, "Orange", getColorBox(Color.ORANGE)),
                new ListItem((byte) 33, "Yellow", getColorBox(Color.YELLOW)),
                new ListItem((byte) 44, "Green", getColorBox(Color.GREEN)),
                new ListItem((byte) 55, "Blue", getColorBox(Color.BLUE)),
                new ListItem((byte) 66, "Cyan", getColorBox(Color.CYAN)),
                new ListItem((byte) 77, "Purple", getColorBox(Color.PURPLE)),
                new ListItem((byte) 94, "Cyan-Purple", getColorBox(Color.CYAN, Color.PURPLE)),
                new ListItem((byte) 106, "Blue-Cyan", getColorBox(Color.BLUE, Color.CYAN)),
                new ListItem((byte) 119, "Green-Blue", getColorBox(Color.GREEN, Color.BLUE)),
                new ListItem((byte) 131, "Yellow-Green", getColorBox(Color.YELLOW, Color.GREEN)),
                new ListItem((byte) 144, "Orange-Yellow", getColorBox(Color.ORANGE, Color.YELLOW)),
                new ListItem((byte) 156, "Red-Orange", getColorBox(Color.RED, Color.ORANGE)),
                new ListItem((byte) 169, "White-Red", getColorBox(Color.WHITE, Color.RED)),
                new ListItem((byte) 200, "All colors", getImageBox("/icon/rainbow.jpg", Color.WHITE))
        );
        return new ListController(controller, universe, fixture, "Color Wheel", items);
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
    private ListController getGoboWheel() {
        var items = List.of(
                new ListItem((byte) 0, "Open", getImageBox("/gobo/open.jpg", Color.BLACK)),
                new ListItem((byte) 16, "Gobo 1", getImageBox("/gobo/gobo-1.jpg", Color.BLACK)),
                new ListItem((byte) 32, "Gobo 2", getImageBox("/gobo/gobo-2.jpg", Color.BLACK)),
                new ListItem((byte) 47, "Gobo 3", getImageBox("/gobo/gobo-3.jpg", Color.BLACK)),
                new ListItem((byte) 63, "Gobo 4", getImageBox("/gobo/gobo-4.jpg", Color.BLACK)),
                new ListItem((byte) 79, "Gobo 5", getImageBox("/gobo/gobo-5.jpg", Color.BLACK)),
                new ListItem((byte) 94, "Gobo 6", getImageBox("/gobo/gobo-6.jpg", Color.BLACK)),
                new ListItem((byte) 110, "Gobo 7", getImageBox("/gobo/gobo-7.jpg", Color.BLACK))

        );
        return new ListController(controller, universe, fixture, "Gobo Wheel", items);
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
    private ListController getPicoSpotProgram() {
        var items = List.of(
                new ListItem((byte) 0, "None", getImageBox("/icon/none.png", Color.WHITE)),
                new ListItem((byte) 50, "White", getColorBox(Color.WHITE)),
                new ListItem((byte) 140, "Program 1", getTextBox("P1")),
                new ListItem((byte) 150, "Program 2", getTextBox("P2")),
                new ListItem((byte) 160, "Program 3", getTextBox("P3")),
                new ListItem((byte) 170, "Program 4", getTextBox("P4")),
                new ListItem((byte) 180, "Program 5", getTextBox("P5")),
                new ListItem((byte) 190, "Program 6", getTextBox("P6")),
                new ListItem((byte) 200, "Program 7", getTextBox("P7")),
                new ListItem((byte) 250, "Sound", getImageBox("/icon/sound-waves.png", Color.WHITE))
        );
        return new ListController(controller, universe, fixture, "Program", items);
    }

    private Pane getTextBox(String text) {
        var stackPane = new StackPane();
        stackPane.setPrefSize(LIST_ITEM_HEIGHT, LIST_ITEM_HEIGHT);
        stackPane.setMinSize(LIST_ITEM_HEIGHT, LIST_ITEM_HEIGHT);
        stackPane.setMaxSize(LIST_ITEM_HEIGHT, LIST_ITEM_HEIGHT);

        var background = new Rectangle(LIST_ITEM_HEIGHT, LIST_ITEM_HEIGHT);
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.DARKGRAY);
        background.setStrokeWidth(1);

        var textNode = new javafx.scene.text.Text(text);
        textNode.setFill(Color.BLACK);
        textNode.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");

        stackPane.getChildren().addAll(background, textNode);
        return stackPane;
    }


    private Pane getImageBox(String img, Color backgroundColor) {
        var stackPane = new StackPane();
        stackPane.setPrefSize(LIST_ITEM_HEIGHT, LIST_ITEM_HEIGHT);
        stackPane.setMinSize(LIST_ITEM_HEIGHT, LIST_ITEM_HEIGHT);
        stackPane.setMaxSize(LIST_ITEM_HEIGHT, LIST_ITEM_HEIGHT);

        var background = new Rectangle(LIST_ITEM_HEIGHT, LIST_ITEM_HEIGHT);
        background.setFill(backgroundColor);

        stackPane.getChildren().addAll(background, ImageHelper.getImageView(img, LIST_ITEM_HEIGHT));
        return stackPane;
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
