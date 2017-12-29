package root.app.data.services;

import javafx.scene.shape.Polygon;
import root.app.model.MarkersPair;
import root.app.model.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Service with geometry for computing zones
 * */
public interface ZoneComputingService {
    String ZONE_PREFIX = "zone_";

    ArrayList<Zone.ChildZone> getChildZones(MarkersPair pair);

    Polygon toFxPolygon(Zone.ChildZone zone, Zone parentZone, int childId);

    List<Polygon> toFxPolygon(Zone zone);

    public List<Double> getPolygonPoints(MarkersPair pair);

    String getChildZoneId(Zone parent, int i);
}
