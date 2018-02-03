package root.app.data.services;

import javafx.scene.shape.Polygon;
import root.app.model.MarkersPair;
import root.app.model.RoadWay;

import java.util.ArrayList;
import java.util.List;

import static root.app.data.services.impl.ImageScaleServiceImpl.*;

/**
 * Service with geometry for computing zones
 * */
public interface ZoneComputingService {
    String ZONE_PREFIX = "zone_";

    List<RoadWay> getRoadWays(MarkersPair pair);

    List<Polygon> toFxPolygon(RoadWay way, ScreenSize screenSize);

    public List<Double> getPolygonPoints(MarkersPair pair);

}
