package root.app.data.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.services.ApplicationStateService;
import root.app.properties.RoadWaysConfigService;

@Service
public class ApplicationStateServiceImpl implements ApplicationStateService {

    private final RoadWaysConfigService roadWaysConfigService;

    @Autowired
    public ApplicationStateServiceImpl(RoadWaysConfigService roadWaysConfigService) {
        this.roadWaysConfigService = roadWaysConfigService;
    }

    @Override
    public void resetZones() {
        roadWaysConfigService.deleteAll();
    }
}
