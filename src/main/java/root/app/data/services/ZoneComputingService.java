package root.app.data.services;

import javafx.scene.shape.Polygon;
import root.app.data.services.impl.ImageScaleServiceImpl;
import root.app.model.MarkersPair;
import root.app.model.Zone;

import java.util.ArrayList;
import java.util.List;

import static root.app.data.services.impl.ImageScaleServiceImpl.*;

/**
 * Service with geometry for computing zones
 * */
public interface ZoneComputingService {
    String ZONE_PREFIX = "zone_";

    ArrayList<Zone.ChildZone> getChildZones(MarkersPair pair);

    List<Polygon> toFxPolygon(Zone zone, ScreenSize screenSize);

    public List<Double> getPolygonPoints(MarkersPair pair);

}
