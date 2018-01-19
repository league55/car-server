package root.app.fx;

import javafx.scene.layout.Pane;
import root.app.model.MarkersPair;

public interface AnchorsService {
    void addAnchorsGroup(Pane imagePane);

    void clean(Pane imagePane);

    MarkersPair getCoordinates(double sceneHeight, double sceneWidth);
}
