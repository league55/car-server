package root.app.data.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.services.CalibrationService;
import root.app.model.Line;
import root.app.model.Point;
import root.app.model.RoadWay;
import root.app.properties.ConfigAttribute;
import root.app.properties.RoadWaysConfigService;

import java.util.List;

@Service
@Slf4j
public class ZonesHeightCalibrationServiceImpl implements CalibrationService {
    private final RoadWaysConfigService zoneConfigService;

    @Autowired
    public ZonesHeightCalibrationServiceImpl(RoadWaysConfigService zoneConfigService) {
        this.zoneConfigService = zoneConfigService;
    }

    @Override
    public void fixPosition(Integer n, Double delta) {
        final List<RoadWay> all = zoneConfigService.findAll();

        fixHorizontal(n, delta, all);
        zoneConfigService.saveAll(all);
    }


    private void fixHorizontal(Integer n, Double deltaY, List<RoadWay> all) {
        all.forEach(zone -> {
            final List<RoadWay.Zone> zones = zone.getZones();
            final RoadWay.Zone bottomZone = zones.get(n);
            final RoadWay.Zone topZone = zones.get(n + 1);

            fixLineY(bottomZone.getPair().getLineB(), deltaY);
            fixLineY(topZone.getPair().getLineA(), deltaY);

            fixXAxis(zone, bottomZone.getPair().getLineB());
            fixXAxis(zone, topZone.getPair().getLineA());
        });
    }

    private void fixXAxis(RoadWay way, Line line2) {
        Line right = new Line(way.getPair().getLineB().getEnd(), way.getPair().getLineA().getEnd());
        Line left = new Line(way.getPair().getLineB().getStart(), way.getPair().getLineA().getStart());

        Point intersection = CalibrationService.intersection(right, line2);
        Point intersection2 = CalibrationService.intersection(left, line2);

        line2.setEnd(intersection);
        line2.setStart(intersection2);
    }


    private void fixLineY(Line line, Double deltaY) {
        line.getStart().setY(line.getStart().getY() + deltaY);
        line.getEnd().setY(line.getEnd().getY() + deltaY);
    }


    @Override
    public boolean canCalibrate(String calibrationType) {
        return ConfigAttribute.ZonesPerLineAmount.name().equals(calibrationType);
    }
}
