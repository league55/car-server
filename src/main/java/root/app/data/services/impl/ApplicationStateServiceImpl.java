package root.app.data.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.services.ApplicationStateService;
import root.app.properties.LineConfigService;
import root.app.properties.RegionConfigService;
import root.app.properties.RoadWaysConfigService;

@Service
public class ApplicationStateServiceImpl implements ApplicationStateService {

    private final RoadWaysConfigService roadWaysConfigService;
    private final RegionConfigService regionConfigService;

    @Autowired
    public ApplicationStateServiceImpl(RoadWaysConfigService roadWaysConfigService, RegionConfigService regionConfigService) {
        this.roadWaysConfigService = roadWaysConfigService;
        this.regionConfigService = regionConfigService;
    }

    @Override
    public void resetZones() {
        roadWaysConfigService.deleteAll();
        regionConfigService.deleteAll();
    }
}
