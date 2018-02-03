package root.app.data.services;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import root.app.data.services.impl.ImageScaleServiceImpl;
import root.app.model.MarkersPair;
import root.app.model.RoadWay;

import java.util.List;

/**
 * Service for drawing different contours, frames, info...
 */
public interface DrawingService {

    void showLines(AnchorPane imageWrapperPane, List<MarkersPair> lines);

    void showZones(AnchorPane imageWrapperPane, List<RoadWay> roadWays, ImageScaleServiceImpl.ScreenSize screenSize);

    void removeZone(AnchorPane imageWrapperPane, RoadWay way);

    void submitRegion(MarkersPair pair, Pane pane);

    void clearAll(AnchorPane imageWrapperPane);
}
