package root.app.properties;

import root.app.model.RoadWay;

import java.util.List;

public interface RoadWaysConfigService extends ConfigService<RoadWay> {

    List<RoadWay> findAll();

    RoadWay.Zone findZone(String zoneId);

    List<RoadWay.Zone> findRow(String rowNum);

    List<RoadWay.Zone> findAllZones();

    void saveZone(RoadWay.Zone zone);

    void saveZones(List<RoadWay.Zone> roadRow);
}
