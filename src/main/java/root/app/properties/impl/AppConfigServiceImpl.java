package root.app.properties.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import root.app.model.AppConfigDTO;
import root.app.model.Zone;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;
import root.app.properties.IOService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

@Component
@Slf4j
public class AppConfigServiceImpl extends ConfigServiceImpl<AppConfigDTO> implements AppConfigService {

    public AppConfigServiceImpl(IOService<List<AppConfigDTO>> saver) {
        super(saver, "config/appProps.yml");
    }


    @Override
    @Cacheable(value = "configCache1")
    public AppConfigDTO findOne(ConfigAttribute attribute) {
        final AppConfigDTO empty = new AppConfigDTO();
        empty.setKey(attribute);

        log.debug("Fetching {}", attribute.name());
        return findAll()
                .stream()
                .filter(c -> c.getKey().equals(attribute))
                .findFirst()
                .orElse(empty);
    }

    @Override
    @Cacheable("configCache2")
    public List<AppConfigDTO> findAll() {
        log.debug("Fetching all configs");
        return super.findAll();
    }

    @Override
    @CacheEvict(value = {"configCache1", "configCache2"}, allEntries = true)
    public Long save(AppConfigDTO dto) {
        AppConfigDTO prevDto = findOne(dto.getKey());
        prevDto.setValue(dto.getValue());

        log.debug("Saving config {}", dto.getKey().name());
        return super.save(prevDto);
    }

    @Override
    @CacheEvict(value = {"configCache1", "configCache2"}, allEntries = true)
    public void delete(Long aLong) {
        super.delete(aLong);
    }

    @Override
    public void delete(AppConfigDTO dto) {
        throw new IllegalStateException("This must not be called");
    }

    @Override
    public void deleteAll() {
        throw new IllegalStateException("This must not be called");
    }

    @Override
    public AppConfigDTO findOne(Long aLong) {
        //search by enum type
        throw new NotImplementedException();
    }
}
