package root.app.data.services;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import root.app.model.Line;
import root.app.model.MarkersPair;
import root.app.model.Point;
import root.app.model.Zone;

import java.util.List;

/**
 * Service for drawing different contours, frames, info...
 */
public interface DrawingService {

    void showLines(AnchorPane imageWrapperPane, List<MarkersPair> lines);

    void showZones(AnchorPane imageWrapperPane, List<Zone> zones);

    void removeZone(AnchorPane imageWrapperPane, Zone zone);

    void submitZone(MarkersPair pair, Pane pane);
}
