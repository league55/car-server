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
public class RoadWidthCalibrationServiceImpl implements CalibrationService {
    private final RoadWaysConfigService zoneConfigService;

    @Autowired
    public RoadWidthCalibrationServiceImpl(RoadWaysConfigService zoneConfigService) {
        this.zoneConfigService = zoneConfigService;
    }

    @Override
    public void fixPosition(Integer n, Double delta) {
        final List<RoadWay> all = zoneConfigService.findAll();

        fixVertical(n, delta, all);
        zoneConfigService.saveAll(all);
    }

    private void fixVertical(Integer n, Double delta, List<RoadWay> all) {
        final List<RoadWay.Zone> roadWayLeft = all.get(n).getZones();
        final List<RoadWay.Zone> roadWayRight = all.get(n + 1).getZones();

        for (int i = 0; i < roadWayLeft.size(); i++) {
            final RoadWay.Zone leftZone = roadWayLeft.get(i);
            final RoadWay.Zone rightZone = roadWayRight.get(i);

            fixLineX(delta, leftZone, rightZone);
        }
        fixLast(delta, roadWayLeft, roadWayRight);

    }

    private void fixLast(Double delta, List<RoadWay.Zone> roadWayLeft, List<RoadWay.Zone> roadWayRight) {
        final double oldTopX = roadWayLeft.get(roadWayLeft.size() - 1).getPair().getLineB().getEnd().getX();
        final double newTopX = oldTopX * (1 - delta / 100);
        roadWayLeft.get(roadWayLeft.size() - 1).getPair().getLineB().getEnd().setX(newTopX);
        roadWayRight.get(roadWayLeft.size() - 1).getPair().getLineB().getStart().setX(newTopX);
    }

    private void fixYAxis(RoadWay way, Line line2) {
        Line top = way.getPair().getLineB();
        Line bot = way.getPair().getLineA();

        Point intersection = CalibrationService.intersection(top, line2);
        Point intersection2 = CalibrationService.intersection(bot, line2);

        line2.setEnd(intersection);
        line2.setStart(intersection2);
    }

    private void fixLineX(Double delta, RoadWay.Zone leftZone, RoadWay.Zone rightZone) {
        double k = 1 - delta / 100;
        final double oldBotX = leftZone.getPair().getLineA().getEnd().getX();
        final double newBottX = oldBotX * k;
        leftZone.getPair().getLineA().getEnd().setX(newBottX);
        rightZone.getPair().getLineA().getStart().setX(newBottX);
    }


    private void fixLineY(Line line, Double deltaY) {
        line.getStart().setY(line.getStart().getY() + deltaY);
        line.getEnd().setY(line.getEnd().getY() + deltaY);
    }

    @Override
    public boolean canCalibrate(String calibrationType) {
        return ConfigAttribute.RoadWaysAmount.name().equals(calibrationType);
    }
}
