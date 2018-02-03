package root.app.properties.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import root.app.model.MarkersPair;
import root.app.model.Point;
import root.app.model.Region;
import root.app.properties.IOService;
import root.app.properties.RegionConfigService;

import java.util.List;

@Service
@Slf4j
public class RegionConfigServiceImpl extends ConfigServiceImpl<Region> implements RegionConfigService {

    public RegionConfigServiceImpl(IOService<List<Region>> saver) {
        super(saver, "../config/regionProps.yml");
    }

    @Override
    @Cacheable("regionCache")
    public List<Region> findAll() {
        log.debug("Looking for all regions");
        return super.findAll();
    }

    @Override
    @Cacheable("regionCache")
    public Region findRegion() {
        log.debug("Looking for all regions");
        final List<Region> all = super.findAll();
        if(all.size() < 1) return null;
        return super.findAll().get(0);
    }

    @Override
    public Region findOne(Long id) {
        log.debug("Looking for all regions");
        return findRegion();
    }

    @Override
    @CacheEvict(value = "regionCache", allEntries = true)
    public Long save(Region way) {
        log.debug("Saving way {}", way.getId());
        final Region region = findRegion() == null ? new Region() : findRegion();

        region.setRegionBounds(way.getRegionBounds());
        region.setOffsets(way.getOffsets());

        return super.save(way);
    }

    @Override
    @CacheEvict(value = "regionCache", allEntries = true)
    public void updateOffset(Integer i, Point offset) {
        final Region region = findRegion();

        region.getOffsets().add(i, offset);
        region.getOffsets().remove(i + 1);
    }

    @Override
    @CacheEvict(value = "regionCache", allEntries = true)
    public void delete(Long aLong) {
        super.delete(aLong);
    }

    @Override
    @CacheEvict(value = "regionCache", allEntries = true)
    public void delete(Region dto) {
        super.delete(dto);
    }

    @Override
    @CacheEvict(value = "regionCache", allEntries = true)
    public void deleteAll() {
        super.deleteAll();
    }

    @Override
    @CacheEvict(value = "regionCache", allEntries = true)
    public void saveAll(List<Region> all) {
        super.saveAll(all);
    }
}
