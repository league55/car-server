package root.app.properties.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import root.app.model.RoadWay;
import root.app.properties.IOService;
import root.app.properties.RoadWaysConfigService;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static root.app.data.services.ZoneComputingService.ZONE_PREFIX;

@Service
@Slf4j
public class RoadWaysConfigServiceImpl extends ConfigServiceImpl<RoadWay> implements RoadWaysConfigService {

    public RoadWaysConfigServiceImpl(IOService<List<RoadWay>> saver) {
        super(saver, "../zoneProps.yml");
    }

    @Override
    @Cacheable(value = "zoneCache")
    public List<RoadWay> findAll() {
        log.debug("Looking for all zones");
        return super.findAll();
    }

    @Override
    @Cacheable("zoneCache2")
    public List<RoadWay.Zone> findAllZones() {
        return findAll().stream().map(RoadWay::getZones).flatMap(Collection::stream).collect(toList());
    }

    @Override
    @CacheEvict(value = {"zoneCache","zoneCache2"}, allEntries = true)
    public Long save(RoadWay way) {
        log.debug("Saving way {}", way.getId());

        return super.save(way);
    }

    @Override
    public RoadWay findOne(Long aLong) {
        return null;
    }

    @Override
    public RoadWay.Zone findZone(String zoneId) {
        //zone_1_1
        String fixedId = zoneId.contains(ZONE_PREFIX) ? zoneId : ZONE_PREFIX + zoneId;
        return findAllZones().stream().filter(zone -> zone.getId().contains(fixedId)).findFirst().orElseThrow(IllegalStateException::new);
    }

    @Override
    public List<RoadWay.Zone> findRow(String rowNum) {
        //zone_1_1
        String fixedId = rowNum.contains(ZONE_PREFIX) ? rowNum : ZONE_PREFIX + rowNum;
        String rowId = ZONE_PREFIX + fixedId.split("_")[2];
        return findAllZones().stream().filter(zone -> zone.getId().contains(rowId)).collect(toList());
    }

    @Override
    @CacheEvict(value = {"zoneCache", "zoneCache2"}, allEntries = true)
    public void saveZones(List<RoadWay.Zone> zones) {
        final List<RoadWay> roadWays = findAll();

        roadWays.forEach(roadWay -> {
            zones.forEach(newZone -> {
                for (int i = 0; i < roadWay.getZones().size(); i++) {
                    if (roadWay.getZones().get(i).getId().equals(newZone.getId())) {
                        roadWay.getZones().set(i, newZone);
                    }
                }
            });
        });

        saveAll(roadWays);
    }

    @Override
    @CacheEvict(cacheNames = {"zoneCache", "zoneCache2"}, allEntries = true)
    public void saveZone(RoadWay.Zone newZone) {
        final List<RoadWay> roadWays = findAll();

        roadWays.get(newZone.getPair().getWayNum() - 1).getZones()
                .stream()
                .filter(zone -> zone.getId().equals(newZone.getId()))
                .forEach(zone -> zone = newZone);

        saveAll(roadWays);
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    @CacheEvict(value = {"zoneCache", "zoneCache2"}, allEntries = true)
    public void delete(Long aLong) {
        super.delete(aLong);
    }

    @Override
    @CacheEvict(value = {"zoneCache", "zoneCache2"}, allEntries = true)
    public void delete(RoadWay dto) {
        super.delete(dto);
    }

    @Override
    @CacheEvict(value = {"zoneCache", "zoneCache2"}, allEntries = true)
    public void deleteAll() {
        super.deleteAll();
    }

    @Override
    @CacheEvict(value = {"zoneCache", "zoneCache2"}, allEntries = true)
    public void saveAll(List<RoadWay> all) {
        super.saveAll(all);
    }
}
