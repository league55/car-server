package root.app.data.runners;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;


/**
 * diff methods to run app
 */
public interface Runner {
    void startCapturing();

    void stopCapturing();

    void setActionButton(Button button);
}
