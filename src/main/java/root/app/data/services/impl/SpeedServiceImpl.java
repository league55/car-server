package root.app.data.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import root.app.data.services.SpeedService;
import root.app.model.Car;

import java.util.List;

/*
* @Deprecated - gives different result depending on FPS
* */
@Slf4j
@Component
public class SpeedServiceImpl implements SpeedService {
    @Override
    public void countSpeed(List<Car> cars, double fps) {
        cars.forEach(car -> {
            if (car.getFistMarkerCrossed() == null || car.getSecondMarkerCrossed() == null || car.getSpeed() != null)
                return;
            final double sec = ((car.getSecondMarkerCrossed() - car.getFistMarkerCrossed()) / 1000.0);
            double speed = car.getDistancePassed() / sec;
            double kmPerHorCoeff = 3.6;
            car.setSpeed(Math.floor(speed * kmPerHorCoeff * 10) / 10);
            log.info("FPS: {}, frames {}, speed: {}, sec: {}", fps, car.getFramesBetweenMarkers(), speed, sec);
        });
    }
}
