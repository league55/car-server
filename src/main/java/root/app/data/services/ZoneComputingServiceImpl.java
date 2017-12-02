package root.app.data.services;

import com.google.common.collect.Lists;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.springframework.stereotype.Service;
import root.app.model.MarkersPair;
import root.app.model.Point;
import root.app.model.Zone;

import java.util.List;
import java.util.function.Function;

@Service
public class ZoneComputingServiceImpl implements ZoneComputingService {
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
                pair.getLineB().getStart().getX(), pair.getLineB().getStart().getY(),
                pair.getLineB().getEnd().getX(), pair.getLineB().getEnd().getY()
        );
        polygon.setFill(Color.RED);
        polygon.setOpacity(0.1);
        return polygon;
    }

}
