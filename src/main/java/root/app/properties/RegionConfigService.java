package root.app.properties;

import org.springframework.cache.annotation.CacheEvict;
import root.app.model.Point;
import root.app.model.Region;

public interface RegionConfigService extends ConfigService<Region> {
    Region findRegion();

    void updateOffset(Integer i, Point offset);
}
