package root.appConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.app.model.AppConfigDTO;
import root.app.model.Zone;
import root.app.properties.ConfigService;
import root.app.properties.IOService;
import root.app.properties.impl.ConfigServiceImpl;

import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    ConfigService<Zone> zoneConfigService(IOService<List<Zone>> saver) {
        return new ConfigServiceImpl<>(saver, "config/zoneProps.yml");
    }

    @Bean
    ConfigService<AppConfigDTO> appConfigService(IOService<List<AppConfigDTO>> saver) {
        return new ConfigServiceImpl<>(saver, "config/appProps.yml");
    }
}
