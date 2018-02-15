package root.app.data.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.services.ReconfiguringService;
import root.app.data.services.ZoneComputingService;
import root.app.model.RoadWay;
import root.app.properties.RoadWaysConfigService;

import java.util.List;

@Service
public class ReconfiguringServiceImpl implements ReconfiguringService {
    @Autowired
    RoadWaysConfigService roadWaysConfigService;
    @Autowired
    ZoneComputingService zoneComputingService;

    @Override
    public void removeRow(int i) {
        int num = i - 1;
        final List<RoadWay> all = roadWaysConfigService.findAll();
        all.forEach(roadWay -> {
            final RoadWay.Zone rowToDelete = roadWay.getZones().get(i);
            roadWay.getZones().get(num - 1).getPair().setLineB(rowToDelete.getPair().getLineB());
            roadWay.getZones().remove(num);
            roadWay.setZones(fixZoneIds(roadWay));
        });

        roadWaysConfigService.saveAll(all);
    }

    @Override
    public void addRow(int i) {

    }

    private List<RoadWay.Zone> fixZoneIds(RoadWay roadWay) {
        List<RoadWay.Zone> zones = roadWay.getZones();
        for (int rowNum = 0; rowNum < zones.size(); rowNum++) {
            RoadWay.Zone zone = zones.get(rowNum);
            zone.setId(zoneComputingService.getChildZoneId(zone.getPair().getWayNum(), rowNum));
        }
        return zones;
    }
}
