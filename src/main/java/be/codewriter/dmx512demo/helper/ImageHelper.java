package be.codewriter.dmx512demo.helper;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageHelper {
    private static final Logger LOGGER = LogManager.getLogger(ImageHelper.class.getName());

    public static ImageView getImageView(String imagePath, int size) {
        try {
            var view = new ImageView(getImage(imagePath));
            view.setFitHeight(size);
            view.setFitWidth(size);
            return view;
        } catch (Exception e) {
            LOGGER.error("Failed to load image {}: {}", imagePath, e.getMessage());
            return new ImageView();
        }
    }

    public static Image getImage(String imagePath) {
        try {
            return new Image(ImageHelper.class.getResourceAsStream(imagePath));
        } catch (Exception e) {
            LOGGER.error("Failed to load image {}: {}", imagePath, e.getMessage());
            return new Image("");
        }
    }
}
