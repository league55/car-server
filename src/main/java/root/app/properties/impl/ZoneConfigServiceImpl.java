package root.app.properties.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import root.app.model.Zone;
import root.app.properties.ConfigService;
import root.app.properties.IOService;

import java.util.List;

@Service
@Slf4j
public class ZoneConfigServiceImpl extends ConfigServiceImpl<Zone> {

    public ZoneConfigServiceImpl(IOService<List<Zone>> saver) {
        super(saver, "../config/zoneProps.yml");
    }

    @Override
    @Cacheable("zoneCache")
    public List<Zone> findAll() {
        log.debug("Looking for all zones");
        return super.findAll();
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public Long save(Zone zone) {
        log.debug("Saving zone {}", zone.getId());

        return super.save(zone);
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public void delete(Long aLong) {
        super.delete(aLong);
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public void delete(Zone dto) {
        super.delete(dto);
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public void deleteAll() {
        super.deleteAll();
    }

}
