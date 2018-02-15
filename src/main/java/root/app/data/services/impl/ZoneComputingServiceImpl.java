package root.app.data.services.impl;

import com.google.common.collect.Lists;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.services.CalibrationService;
import root.app.data.services.ImageScaleService;
import root.app.data.services.ZoneComputingService;
import root.app.model.*;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;
import root.utils.DeepCopy;

import java.util.ArrayList;
import java.util.List;

import static root.app.data.services.impl.ImageScaleServiceImpl.*;

@Service
public class ZoneComputingServiceImpl implements ZoneComputingService {

    private final AppConfigService appConfigService;
    private final ImageScaleService scaleService;
    private final CalibrationService calibrationService;

    @Autowired
    public ZoneComputingServiceImpl(AppConfigService appConfigService, ImageScaleService scaleService, CalibrationService calibrationService) {
        this.appConfigService = appConfigService;
        this.scaleService = scaleService;
        this.calibrationService = calibrationService;
    }

    @Override
    public List<RoadWay> getRoadWays(MarkersPair pair) {
        Integer zonesPerLine = Integer.parseInt(appConfigService.findOne(ConfigAttribute.ZonesPerLineAmount).getValue());
        Integer ways = Integer.valueOf(appConfigService.findOne(ConfigAttribute.RoadWaysAmount).getValue());

        List<MarkersPair> basePairs = getBasePairs((MarkersPair) DeepCopy.copy(pair), ways);

        List<RoadWay> roadWays = Lists.newArrayList();
        for (int wayNum = 0; wayNum < basePairs.size(); wayNum++) {
            MarkersPair pair1 = basePairs.get(wayNum);
            roadWays.add(getRoadWay(zonesPerLine, pair1, pair1.getLineB(), wayNum + 1));
        }

        return roadWays;
    }

    private RoadWay getRoadWay(Integer zonesPerLine, MarkersPair pair, Line lastLine, int wayNum) {
        RoadWay roadWay = new RoadWay();
        final ArrayList<RoadWay.Zone> zones = Lists.newArrayList();
        MarkersPair basePair = (MarkersPair) DeepCopy.copy(pair);

        for (Integer zoneNum = 0; zoneNum < zonesPerLine - 1; zoneNum++) {
            final double k = 1.0 / (zonesPerLine - zoneNum - 1);

            final MarkersPair childZonePair = getChildZonePair(basePair, k, wayNum);
            final RoadWay.Zone z = new RoadWay.Zone(getChildZoneId(wayNum, zoneNum), childZonePair);
            zones.add(z);

            basePair.setLineA(childZonePair.getLineB());
        }
        //add last zone
        zones.add(getLastZone(basePair.getLineA(), lastLine, wayNum, zonesPerLine));

        roadWay.setPair(pair);
        roadWay.setZones(zones);

        return roadWay;
    }

    private List<MarkersPair> getBasePairs(MarkersPair clone, Integer ways) {
        List<MarkersPair> basePairs = Lists.newArrayList();

        double delta_A_X = (clone.getLineA().getEnd().getX() - clone.getLineA().getStart().getX()) / ways;
        double delta_B_X = (clone.getLineB().getEnd().getX() - clone.getLineB().getStart().getX()) / ways;

        for (Integer i = 0; i < ways; i++) {
            MarkersPair template = (MarkersPair) DeepCopy.copy(clone);
            MarkersPair pair = (MarkersPair) DeepCopy.copy(clone);

            pair.setWayNum(i);

            //counting X
            pair.getLineA().getStart().setX(template.getLineA().getStart().getX() + delta_A_X * i);
            pair.getLineA().getEnd().setX(template.getLineA().getStart().getX() + delta_A_X * (i + 1));

            pair.getLineB().getStart().setX(template.getLineB().getStart().getX() + delta_B_X * i);
            pair.getLineB().getEnd().setX(template.getLineB().getStart().getX() + delta_B_X * (i + 1));


            //fixing Y
            final Line left = new Line(pair.getLineA().getStart(), pair.getLineB().getStart());
            final Line right = new Line(pair.getLineA().getEnd(), pair.getLineB().getEnd());
            pair.getLineA().setStart(calibrationService.intersection(clone.getLineA(), left));
            pair.getLineA().setEnd(calibrationService.intersection(clone.getLineA(), right));

            pair.getLineB().setStart(calibrationService.intersection(clone.getLineB(), left));
            pair.getLineB().setEnd(calibrationService.intersection(clone.getLineB(), right));

            basePairs.add(pair);

        }

        return basePairs;
    }


    private MarkersPair getChildZonePair(MarkersPair parentPair, double k, Integer wayNum) {
        double x1 = (parentPair.getLineA().getStart().getX() + parentPair.getLineB().getStart().getX() * k) / (1.0 + k);
        double y1 = (parentPair.getLineA().getStart().getY() + parentPair.getLineB().getStart().getY() * k) / (1.0 + k);

        double x2 = (parentPair.getLineA().getEnd().getX() + parentPair.getLineB().getEnd().getX() * k) / (1.0 + k);
        double y2 = (parentPair.getLineA().getEnd().getY() + parentPair.getLineB().getEnd().getY() * k) / (1.0 + k);

        final Double windowHeight = parentPair.getLineA().getStart().getWindowHeight();
        final Double windowWidth = parentPair.getLineA().getStart().getWindowWidth();

        final Point start = new Point(x1, y1, windowHeight, windowWidth);
        final Point end = new Point(x2, y2, windowHeight, windowWidth);

        final MarkersPair pair = new MarkersPair(parentPair.getLineA(), new Line(start, end));

        pair.setWayNum(wayNum);

        return pair;
    }


    private @NotNull RoadWay.Zone getLastZone(Line start, Line end, int wayNum, Integer zonesPerLine) {
        final MarkersPair lastPair = new MarkersPair(start, end);
        lastPair.setWayNum(wayNum);
        String zoneId = getChildZoneId(wayNum, zonesPerLine - 1);
        return new RoadWay.Zone(zoneId, true, lastPair);
    }

    private Polygon toFxPolygon(RoadWay.Zone zone, ScreenSize screenSize) {

        final MarkersPair pair = scaleService.fixedSize(screenSize, zone).getPair();

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
        polygon.setId(zone.getId());
        return polygon;
    }

    @Override
    public List<Polygon> toFxPolygons(List<RoadWay.Zone> zones , ScreenSize screenSize) {
        List<Polygon> polygons = Lists.newArrayList();
        for (RoadWay.Zone child : zones) {
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

    @Override
    @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.Contract(pure = true)
    public String getChildZoneId(Integer wayNum, Integer zoneNum) {
        return ZONE_PREFIX + wayNum + "_" + (zoneNum + 1);
    }

}
