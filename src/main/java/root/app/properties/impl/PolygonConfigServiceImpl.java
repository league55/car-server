package root.app.properties.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import root.app.model.PolygonDTO;
import root.app.properties.IOService;
import root.app.properties.PolygonConfigService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

@Service
@Slf4j
public class PolygonConfigServiceImpl extends ConfigServiceImpl<PolygonDTO> implements PolygonConfigService {


    public PolygonConfigServiceImpl(IOService<List<PolygonDTO>> saver) {
        super(saver, "../polygons.yml");
    }

    @Override
    @Cacheable(value = "polygonCache1")
    public PolygonDTO findOne(PolygonDTO.Destination destination) {
        if(destination == null) {
            throw new IllegalStateException("Polygon destination must not be null");
        }

        final PolygonDTO empty = new PolygonDTO();
        empty.setDestination(destination);

        log.debug("Fetching {}", destination.name());
        return findAll()
                .stream()
                .filter(c -> c.getDestination().equals(destination))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Cacheable("polygonCache2")
    public List<PolygonDTO> findAll() {
        log.debug("Fetching all configs");
        return super.findAll();
    }

    @Override
    @CacheEvict(value = {"polygonCache1", "polygonCache2"}, allEntries = true)
    public Long save(PolygonDTO dto) {
        if(dto.getDestination() == null) {
            throw new IllegalStateException("Polygon destination must not be null");
        }

        PolygonDTO prevDto = findOne(dto.getDestination());

        if(prevDto == null) {
            prevDto = dto;
        }

        prevDto.setTopLeft(dto.getTopLeft());
        prevDto.setTopRight(dto.getTopRight());
        prevDto.setBotRight(dto.getBotRight());
        prevDto.setBotLeft(dto.getBotLeft());

        log.debug("Saving config {}", dto.getDestination().name());
        return super.save(prevDto);
    }

    @Override
    @CacheEvict(value = {"polygonCache1", "polygonCache2"}, allEntries = true)
    public void delete(Long aLong) {
        super.delete(aLong);
    }

    @Override
    public void delete(PolygonDTO dto) {
        throw new IllegalStateException("This must not be called");
    }

    @Override
    public void deleteAll() {
        throw new IllegalStateException("This must not be called");
    }

    @Override
    public PolygonDTO findOne(Long aLong) {
        //search by enum type
        throw new NotImplementedException();
    }
}
