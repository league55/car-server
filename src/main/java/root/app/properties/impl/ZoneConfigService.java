package root.app.properties.impl;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Repository;
import root.app.model.Zone;
import root.app.properties.IOService;

import java.util.List;

@Repository
public class ZoneConfigService extends ConfigServiceImpl<Zone> {
    private List<Zone> zoneCache = Lists.newArrayList();

    public ZoneConfigService(IOService<List<Zone>> saver) {
        super(saver, "config/zoneProps.yml");
        zoneCache = super.findAll();
    }

    @Override
    public List<Zone> findAll() {
        return zoneCache;
    }

    @Override
    public Long save(Zone pair) {
        final Long save = super.save(pair);

        invalidateCache();

        return save;
    }

    @Override
    public void delete(Long aLong) {
        super.delete(aLong);
        invalidateCache();
    }

    @Override
    public void delete(Zone markersPair) {
        super.delete(markersPair);
        invalidateCache();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        invalidateCache();
    }

    private void invalidateCache() {
        zoneCache = super.findAll();
    }
}
