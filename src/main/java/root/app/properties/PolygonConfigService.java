package root.app.properties;

import root.app.model.PolygonDTO;

/**
 * Lines specific methods
 */
public interface PolygonConfigService extends ConfigService<PolygonDTO> {
    PolygonDTO findOne(PolygonDTO.Destination attribute);
}
