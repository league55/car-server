package root.app.data.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.app.data.services.SpeedService;
import root.app.model.AppConfigDTO;
import root.app.model.Car;
import root.app.model.CrossedPair;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;

import java.util.List;


@Slf4j
@Component
public class SpeedServiceImpl implements SpeedService {

    @Autowired
    private AppConfigService appConfigService;

    @Override
    public void countSpeed(List<Car> cars) {
        cars.forEach(car -> {
            car.getCrossedPairs().stream().filter(cp -> cp.getSpeed() == null && cp.getTimeEnteredZone() != null && cp.getTimeLeavedZone() != null)
                    .forEach(crossedPair -> crossedPair.setSpeed(countSpeed(crossedPair)));
        });
    }

    private Double countSpeed(CrossedPair crossedPair) {
        final double sec = ((crossedPair.getTimeLeavedZone() - crossedPair.getTimeEnteredZone()) / 1000.0);

        final AppConfigDTO one = appConfigService.findOne(ConfigAttribute.ZoneHeight);
        final Double value = Double.valueOf(one.getValue());
        double speed = value / sec;
        double kmPerHorCoeff = 3.6;

        final double speedKmPerH = Math.floor(speed * kmPerHorCoeff * 10) / 10;
        log.info("speed:{} | sec: {}", speed, sec);
        return speedKmPerH;
    }
}
