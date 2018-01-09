package root.app.fx;

import javafx.scene.layout.Pane;
import root.app.model.MarkersPair;

public interface AnchorsService {
    void addNewZone(Pane imagePane);

    void clean(Pane imagePane);

    MarkersPair getCoordinates(double sceneHeight, double sceneWidth);
}
