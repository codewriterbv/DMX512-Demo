package be.codewriter.dmx512demo.helper;

import javafx.scene.paint.Color;

public class ColorHelper {
    private ColorHelper() {
        // Hide constructor
    }

    public static String getCssRGBA(Color color, double opacity) {
        return getCssRGBA((int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                opacity);
    }

    public static String getCssRGBA(int red, int green, int blue, double opacity) {
        return "rgb(" + red + "," + green + "," + blue + "," + opacity + ")";
    }
}
