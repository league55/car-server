package root.app.properties.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.model.MarkersPair;
import root.app.properties.IOService;
import root.app.properties.LineConfigService;

import java.util.List;


@Slf4j
@Service
public class LineConfigServiceImpl extends ConfigServiceImpl<MarkersPair> implements LineConfigService {
    private static final String fileName = "lineMarkersProps.yml";

    @Autowired
    public LineConfigServiceImpl(IOService<List<MarkersPair>> saver) {
        super(saver, fileName);
    }

    @Override
    public void updateLeftDistance(Long id, Integer distance) {
        MarkersPair markersPair = findOne(id);

        if (markersPair != null) {
            markersPair.setDistanceLeft(distance);
            save(markersPair);
        } else {
            log.error("Can't update distanceLeft for {}", id);
        }
    }

    @Override
    public void updateWayNumber(Long id, Integer wayNum) {
        MarkersPair markersPair = findOne(id);

        if (markersPair != null) {
            markersPair.setWayNum(wayNum);
            save(markersPair);
        } else {
            log.error("Can't update wayNum for {}", id);
        }
    }

}

