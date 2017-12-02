package root.app.properties;

import root.app.model.MarkersPair;

/**
 * Lines specific methods
 */
public interface LineConfigService extends ConfigService<MarkersPair> {
    void updateLeftDistance(Long id, Integer distance);
    void updateWayNumber(Long id, Integer wayNum);
}
