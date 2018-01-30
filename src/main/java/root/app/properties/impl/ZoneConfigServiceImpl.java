package root.app.properties.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import root.app.model.RoadWay;
import root.app.properties.IOService;

import java.util.List;

@Service
@Slf4j
public class ZoneConfigServiceImpl extends ConfigServiceImpl<RoadWay> {

    public ZoneConfigServiceImpl(IOService<List<RoadWay>> saver) {
        super(saver, "../config/zoneProps.yml");
    }

    @Override
    @Cacheable("zoneCache")
    public List<RoadWay> findAll() {
        log.debug("Looking for all zones");
        return super.findAll();
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public Long save(RoadWay way) {
        log.debug("Saving way {}", way.getId());

        return super.save(way);
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public void delete(Long aLong) {
        super.delete(aLong);
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public void delete(RoadWay dto) {
        super.delete(dto);
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public void deleteAll() {
        super.deleteAll();
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public void saveAll(List<RoadWay> all) {
        super.saveAll(all);
    }

}
