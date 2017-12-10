package root.app.data.services.impl;

import com.google.common.collect.Lists;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.services.ZoneComputingService;
import root.app.model.MarkersPair;
import root.app.model.Zone;
import root.app.properties.LineConfigService;

import java.util.List;

@Service
public class ZoneComputingServiceImpl implements ZoneComputingService {
    private static final String ZONE_PREFIX = "zone_";

    @Autowired
    private LineConfigService lineConfigService;

    @Override
    public List<Zone> getChildZones(MarkersPair pair, int zoneAmount) {
        return Lists.newArrayList(new Zone());
    }

    @Override
    public Polygon toFxPolygon(Zone zone) {
        final MarkersPair pair = zone.getPair();

        final Polygon polygon = new Polygon(
                pair.getLineA().getStart().getX(), pair.getLineA().getStart().getY(),
                pair.getLineA().getEnd().getX(), pair.getLineA().getEnd().getY(),
                pair.getLineB().getEnd().getX(), pair.getLineB().getEnd().getY(),
                pair.getLineB().getStart().getX(), pair.getLineB().getStart().getY()
                );
        polygon.setFill(Color.RED);
        polygon.setOpacity(0.1);
        polygon.setId(ZONE_PREFIX + zone.getId());
        return polygon;
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

}
