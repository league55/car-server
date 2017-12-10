package root.app.data.services;

import javafx.scene.shape.Polygon;
import root.app.model.MarkersPair;
import root.app.model.Zone;

import java.util.List;

/**
 * Service with geometry for computing zones
 * */
public interface ZoneComputingService {
    List<Zone> getChildZones(MarkersPair pair, int zoneAmount);

    Polygon toFxPolygon(Zone zone);

    public List<Double> getPolygonPoints(MarkersPair pair);
}
