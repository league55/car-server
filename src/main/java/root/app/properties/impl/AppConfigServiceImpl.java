package root.app.properties.impl;

import org.springframework.stereotype.Component;
import root.app.model.AppConfigDTO;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;
import root.app.properties.IOService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

@Component
public class AppConfigServiceImpl extends ConfigServiceImpl<AppConfigDTO> implements AppConfigService {

    public AppConfigServiceImpl(IOService<List<AppConfigDTO>> saver) {
        super(saver, "config/appProps.yml");
    }


    @Override
    public AppConfigDTO findOne(ConfigAttribute attribute) {
        final AppConfigDTO empty = new AppConfigDTO();
        empty.setKey(attribute);

        return findAll()
                .stream()
                .filter(c -> c.getKey().equals(attribute))
                .findFirst()
                .orElse(empty);
    }

    @Override
    public Long save(AppConfigDTO dto) {
        AppConfigDTO prevDto = findOne(dto.getKey());
        prevDto.setValue(dto.getValue());

        return super.save(prevDto);
    }

    @Override
    public AppConfigDTO findOne(Long aLong) {
        throw new NotImplementedException();
    }
}
