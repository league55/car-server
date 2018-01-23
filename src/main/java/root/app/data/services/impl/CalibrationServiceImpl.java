package root.app.data.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import root.app.data.services.CalibrationService;
import root.app.model.Line;
import root.app.model.Point;
import root.app.model.Zone;
import root.app.properties.ConfigService;

import java.util.List;

@Service
public class CalibrationServiceImpl implements CalibrationService {
    private final ConfigService<Zone> zoneConfigService;

    @Autowired
    public CalibrationServiceImpl(@Qualifier("zoneConfigServiceImpl") ConfigService<Zone> zoneConfigService) {
        this.zoneConfigService = zoneConfigService;
    }

    @Override
    public void fixPosition(Integer n, Double deltaY) {
        final List<Zone> all = zoneConfigService.findAll();
        all.forEach(zone -> {
            final List<Zone.ChildZone> childZones = zone.getChildZones();
            final Zone.ChildZone bottomZone = childZones.get(n);
            final Zone.ChildZone topZone = childZones.get(n + 1);

            fixLine(bottomZone.getPair().getLineB(), deltaY);
            fixLine(topZone.getPair().getLineA(), deltaY);

            fixXAxis(zone, bottomZone.getPair().getLineB());
            fixXAxis(zone, topZone.getPair().getLineA());
        });

        zoneConfigService.saveAll(all);
    }

    private void fixXAxis(Zone zone, Line line2) {
        Line right = new Line(zone.getPair().getLineB().getEnd(), zone.getPair().getLineA().getEnd());
        Line left = new Line(zone.getPair().getLineB().getStart(), zone.getPair().getLineA().getStart());

        Point intersection = intersection(right, line2);
        Point intersection2 = intersection(left, line2);

        line2.setEnd(intersection);
        line2.setStart(intersection2);
    }

    private void fixLine(Line line, Double deltaY) {
        line.getStart().setY(line.getStart().getY() + deltaY);
        line.getEnd().setY(line.getEnd().getY() + deltaY);
    }

    private Point intersection(Line lineA, Line lineB) {
        Point A = lineA.getStart();
        Point B = lineA.getEnd();
        Point C = lineB.getStart();
        Point D = lineB.getEnd();

        double denominator = (A.getX() - B.getX()) * (C.getY() - D.getY()) - (A.getY() - B.getY()) * (C.getX() - D.getX());

        if (denominator != 0) {
            double px = ((A.getX() * B.getY() - A.getY() * B.getX()) * (C.getX() - D.getX()) - (A.getX() - B.getX())
                    * (C.getX() * D.getY() - C.getY() * D.getX()))
                    / denominator;
            double py = ((A.getX() * B.getY() - A.getY() * B.getX()) * (C.getY() - D.getY()) - (A.getY() - B.getY())
                    * (C.getX() * D.getY() - C.getY() * D.getX()))
                    / denominator;

            return new Point(px, py, D.getWindowHeight(), D.getWindowWidth());

        }

        return null;
    }
}
