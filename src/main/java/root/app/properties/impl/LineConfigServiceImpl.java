package root.app.properties.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import root.app.model.MarkersPair;
import root.app.model.Zone;
import root.app.properties.IOService;
import root.app.properties.LineConfigService;

import java.util.List;


@Slf4j
@Service
public class LineConfigServiceImpl extends ConfigServiceImpl<MarkersPair> implements LineConfigService {
    private static final String fileName = "config/lineMarkersProps.yml";

    @Autowired
    public LineConfigServiceImpl(IOService<List<MarkersPair>> saver) {
        super(saver, fileName);
    }

    @Override
    @Cacheable("pairsCache")
    public MarkersPair findOne(Long aLong) {
        return super.findOne(aLong);
    }

    @Override
    @Cacheable("pairsCache2")
    public List<MarkersPair> findAll() {
        return super.findAll();
    }

    @Override
    @CacheEvict(value = {"pairsCache", "pairsCache2"}, allEntries = true)
    public void updateLeftDistance(Long id, Integer distance) {
        MarkersPair markersPair = findOne(id);

        if (markersPair != null) {
            markersPair.setDistanceLeft(distance);
            save(markersPair);
        } else {
            log.error("Can't update distanceLeft for {}", id);
        }
    }

    @Override
    @CacheEvict(value = {"pairsCache", "pairsCache2"}, allEntries = true)
    public void updateWayNumber(Long id, Integer wayNum) {
        MarkersPair markersPair = findOne(id);

        if (markersPair != null) {
            markersPair.setWayNum(wayNum);
            save(markersPair);
        } else {
            log.error("Can't update wayNum for {}", id);
        }
    }

    @Override
    @CacheEvict(value = {"pairsCache", "pairsCache2"}, allEntries = true)
    public Long save(MarkersPair pair) {
        return super.save(pair);
    }

    @Override
    @CacheEvict(value = {"pairsCache", "pairsCache2"}, allEntries = true)
    public void delete(Long aLong) {
        super.delete(aLong);
    }

    @Override
    @CacheEvict(value = {"pairsCache", "pairsCache2"}, allEntries = true)
    public void delete(MarkersPair dto) {
        super.delete(dto);
    }

    @Override
    @CacheEvict(value = {"pairsCache", "pairsCache2"}, allEntries = true)
    public void deleteAll() {
        super.deleteAll();
    }

}

