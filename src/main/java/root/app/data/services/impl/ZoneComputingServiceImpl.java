package root.app.data.services.impl;

import com.google.common.collect.Lists;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.services.ZoneComputingService;
import root.app.model.*;
import root.app.properties.ConfigAttribute;
import root.app.properties.ConfigService;
import root.app.properties.LineConfigService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ZoneComputingServiceImpl implements ZoneComputingService {
    private static final String ZONE_PREFIX = "zone_";

    @Autowired
    private ConfigService<AppConfigDTO> appConfigService;

    @Autowired
    private LineConfigService lineConfigService;

    @Override
    public ArrayList<Zone.ChildZone> getChildZones(MarkersPair pair) {
        final Line lastLine = pair.getLineB();
        AppConfigDTO<String> zonesPerLineConfig = appConfigService.findAll().stream().filter(c -> ConfigAttribute.ZonesPerLineAmount.equals(c.getKey())).findFirst().get();
        Integer zonesPerLine = Integer.parseInt(zonesPerLineConfig.getValue());
        final ArrayList<Zone.ChildZone> zones = Lists.newArrayList();

        for (Integer i = 0; i < zonesPerLine - 1; i++) {
            final double k = 1.0 / (zonesPerLine - i - 1);

            final MarkersPair childZonePair = getChildZonePair(pair, k);
            final Zone.ChildZone z = new Zone.ChildZone(childZonePair);
            zones.add(z);

            pair.setLineA(childZonePair.getLineB());
        }
        //add last zone
        zones.add(getLastZone(pair.getLineA(), lastLine));
        return zones;
    }


    private MarkersPair getChildZonePair(MarkersPair parentPair, double k) {
        double x1 = (parentPair.getLineA().getStart().getX() + parentPair.getLineB().getStart().getX() * k) / (1.0 + k);
        double y1 = (parentPair.getLineA().getStart().getY() + parentPair.getLineB().getStart().getY() * k) / (1.0 + k);

        double x2 = (parentPair.getLineA().getEnd().getX() + parentPair.getLineB().getEnd().getX() * k) / (1.0 + k);
        double y2 = (parentPair.getLineA().getEnd().getY() + parentPair.getLineB().getEnd().getY() * k) / (1.0 + k);

        final Double windowHeight = parentPair.getLineA().getStart().getWindowHeight();
        final Double windowWidth = parentPair.getLineA().getStart().getWindowWidth();

        final Point start = new Point(x1, y1, windowHeight, windowWidth);
        final Point end = new Point(x2, y2, windowHeight, windowWidth);

        final MarkersPair pair = new MarkersPair(parentPair.getLineA(), new Line(start, end));
        final Long saved = lineConfigService.save(pair);
        return lineConfigService.findOne(saved);
    }

    private Zone.ChildZone getLastZone(Line start, Line end) {
        final MarkersPair lastPair = new MarkersPair(start, end);
        final Long saved = lineConfigService.save(lastPair);
        return new Zone.ChildZone(lineConfigService.findOne(saved));
    }

    @Override
    public Polygon toFxPolygon(Zone.ChildZone zone, Zone parent, int childId) {

        final MarkersPair pair = zone.getPair();

        final Polygon polygon = new Polygon(
                pair.getLineA().getStart().getX(), pair.getLineA().getStart().getY(),
                pair.getLineA().getEnd().getX(), pair.getLineA().getEnd().getY(),
                pair.getLineB().getEnd().getX(), pair.getLineB().getEnd().getY(),
                pair.getLineB().getStart().getX(), pair.getLineB().getStart().getY()
        );
        polygon.setFill(Color.RED);
        polygon.setOpacity(0.1);
        polygon.setStroke(Color.GREEN);
        polygon.setStrokeWidth(5);
        polygon.setId(getChildZoneId(parent, childId));
        return polygon;
    }

    @Override
    public List<Polygon> toFxPolygon(Zone zone) {
        List<Zone.ChildZone> childZones = zone.getChildZones();
        List<Polygon> polygons = Lists.newArrayList();
        for (int i = 0; i < childZones.size(); i++) {
            Zone.ChildZone child = childZones.get(i);
            polygons.add(toFxPolygon(child, zone, i));
        }
        return polygons;
    }

    @Override
    public List<Double> getPolygonPoints(MarkersPair pair) {
        return Lists.newArrayList(
                pair.getLineA().getStart().getX(), pair.getLineA().getStart().getY(),
                pair.getLineA().getEnd().getX(), pair.getLineA().getEnd().getY(),
                pair.getLineB().getEnd().getX(), pair.getLineB().getEnd().getY(),
                pair.getLineB().getStart().getX(), pair.getLineB().getStart().getY()
        );
    }

    @Override
    public String getChildZoneId(Zone parent, int i) {
        return ZONE_PREFIX + parent.getId() + "_" + i;
    }

}
