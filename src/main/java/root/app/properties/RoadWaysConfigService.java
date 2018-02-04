package root.app.properties;

import root.app.model.RoadWay;

import java.util.List;

public interface RoadWaysConfigService extends ConfigService<RoadWay> {

    RoadWay.Zone findZone(String zoneId);

    List<RoadWay.Zone> findRow(String zoneId);

    List<RoadWay.Zone> findAllZones();

    void saveZone(RoadWay.Zone zone);

    void saveZones(List<RoadWay.Zone> roadRow);
}
