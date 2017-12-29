package root.app.properties;

import root.app.model.AppConfigDTO;
import root.app.model.MarkersPair;

/**
 * Lines specific methods
 */
public interface AppConfigService extends ConfigService<AppConfigDTO> {
    AppConfigDTO findOne(ConfigAttribute attribute);
}
