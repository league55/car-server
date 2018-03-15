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

    void showZones(AnchorPane imageWrapperPane, ImageScaleServiceImpl.ScreenSize screenSize);

    void submitRegion(MarkersPair pair);

    void clearAll(AnchorPane imageWrapperPane);
}
