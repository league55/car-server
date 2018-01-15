package root.app.properties.impl;

import com.google.common.collect.Lists;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import root.app.model.Zone;
import root.app.properties.IOService;

import java.util.List;

@Repository
public class ZoneConfigService extends ConfigServiceImpl<Zone> {

    public ZoneConfigService(IOService<List<Zone>> saver) {
        super(saver, "config/zoneProps.yml");
    }

    @Override
    @Cacheable("zoneCache")
    public List<Zone> findAll() {
        return super.findAll();
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public Long save(Zone pair) {
        return super.save(pair);
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public void delete(Long aLong) {
        super.delete(aLong);
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public void delete(Zone markersPair) {
        super.delete(markersPair);
    }

    @Override
    @CacheEvict(value = "zoneCache", allEntries = true)
    public void deleteAll() {
        super.deleteAll();
    }

}
