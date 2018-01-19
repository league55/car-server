package root.app.data.services.impl;

import com.google.common.collect.Lists;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.services.ImageScaleService;
import root.app.data.services.ZoneComputingService;
import root.app.model.*;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;
import root.app.properties.LineConfigService;

import java.util.ArrayList;
import java.util.List;

import static root.app.data.services.impl.ImageScaleServiceImpl.*;

@Service
public class ZoneComputingServiceImpl implements ZoneComputingService {

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private LineConfigService lineConfigService;
    @Autowired
    private ImageScaleService scaleService;

    @Override
    public ArrayList<Zone.ChildZone> getChildZones(MarkersPair pair) {
        final Line lastLine = pair.getLineB();
        AppConfigDTO zonesPerLineConfig = appConfigService.findOne(ConfigAttribute.ZonesPerLineAmount);
        Integer zonesPerLine = Integer.parseInt(zonesPerLineConfig.getValue());
        final ArrayList<Zone.ChildZone> zones = Lists.newArrayList();

        for (Integer i = 0; i < zonesPerLine - 1; i++) {
            final double k = 1.0 / (zonesPerLine - i - 1);

            final MarkersPair childZonePair = getChildZonePair(pair, k);
            final Zone.ChildZone z = new Zone.ChildZone(getChildZoneId(childZonePair, i), childZonePair);
            zones.add(z);

            pair.setLineA(childZonePair.getLineB());
        }
        //add last zone
        zones.add(getLastZone(pair.getLineA(), lastLine, zonesPerLine));
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

    private Zone.ChildZone getLastZone(Line start, Line end, int i) {
        final MarkersPair lastPair = new MarkersPair(start, end);
        final Long saved = lineConfigService.save(lastPair);
        String zoneId = getChildZoneId(lineConfigService.findOne(saved), i);
        return new Zone.ChildZone(zoneId, true, lineConfigService.findOne(saved));
    }

    private Polygon toFxPolygon(Zone.ChildZone childZone, ScreenSize screenSize) {

        final MarkersPair pair = scaleService.fixedSize(screenSize, childZone).getPair();

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
        polygon.setId(childZone.getId());
        return polygon;
    }

    @Override
    public List<Polygon> toFxPolygon(Zone zone, ScreenSize screenSize) {
        List<Zone.ChildZone> childZones = zone.getChildZones();
        List<Polygon> polygons = Lists.newArrayList();
        for (Zone.ChildZone child : childZones) {
            polygons.add(toFxPolygon(child, screenSize));
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

    private String getChildZoneId(MarkersPair pair, Integer i) {
        return ZONE_PREFIX + pair.getId() + "_" + i;
    }

}
