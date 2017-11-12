package root.app.data.services.impl;

import lombok.extern.slf4j.Slf4j;
import root.app.data.services.SpeedService;
import root.app.model.Car;

import java.util.List;
import java.util.function.DoubleFunction;

@Slf4j
public class FPSSpeedService implements SpeedService {
    private DoubleFunction<Double> roundSpeed = speed -> Math.floor(speed * 10) / 10;

    @Override
    public void countSpeed(List<Car> cars, double fps) {

        cars.forEach(car -> {
            if (car.getFistMarkerCrossed() == null && car.getSecondMarkerCrossed() == null) return;

            if (car.getFistMarkerCrossed() != null && car.getSecondMarkerCrossed() == null) {
                car.setFramesAfterFirstMarker(car.getFramesAfterFirstMarker() + 1);
            }

            if (car.getFistMarkerCrossed() != null && car.getSecondMarkerCrossed() == null) {
                car.setFramesBetweenMarkers(car.getFramesAfterFirstMarker());
            }

            if (car.getFistMarkerCrossed() != null && car.getSecondMarkerCrossed() != null && car.getSpeed() == null) {

                double sec = car.getFramesBetweenMarkers() / fps;
                double speed = 50 / sec * 3.6;
                car.setSpeed(roundSpeed.apply(speed));

                log.debug("{}, {}", sec, (car.getSecondMarkerCrossed() - car.getFistMarkerCrossed()) / 1000.0);
                log.debug("FPS: {}, frames {}, speed: {}, sec: {}", fps, car.getFramesBetweenMarkers(), speed, sec);
                log.info("FPS: {}, frames {}, speed: {}, sec: {}", fps, car.getFramesBetweenMarkers(), speed, sec);
            }
        });
    }
}