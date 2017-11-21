package root.app.data.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Point;
import org.springframework.stereotype.Component;
import root.app.data.services.LineCrossingService;
import root.app.model.Car;
import root.app.model.Line;
import root.app.model.MarkersPair;

import java.util.List;

@Slf4j
@Component
public class LineCrossingServiceImpl implements LineCrossingService {

    @Override
    public long countCars(List<Car> newCarsList) {

        return newCarsList.stream()
                .filter(car -> car.getFistMarkerCrossed() != null && car.getSecondMarkerCrossed() != null).count();
    }

    @Override
    public void findCrossingLineCars(List<Car> cars, List<MarkersPair> lines) {
        lines.forEach(pair -> {

            cars.forEach(car -> {
                if (car.isStillTracked && car.centerPositions.size() >= 2) {
                    if (isCarCrossedLine(pair.getLineA(), car)) {
                        car.setFistMarkerCrossed(System.currentTimeMillis());
                    }

                    if (isCarCrossedLine(pair.getLineB(), car)) {
                        car.setSecondMarkerCrossed(System.currentTimeMillis());
                        car.setPassedPair(pair);
                    }
                }
            });

        });
    }

    //TODO: count not perfect horizontal lines
    private boolean isCarCrossedLine(Line line, Car car) {
        int prevFrameIndex = car.centerPositions.size() - 2;
        int currFrameIndex = car.centerPositions.size() - 1;

        double lineY = line.getEnd().getY();
        double lineStartX = line.getStart().getX();
        double lineEndX = line.getEnd().getX();

        Point previousePosition = car.centerPositions.get(prevFrameIndex);
        Point currentPosition = car.centerPositions.get(currFrameIndex);

        boolean carCrossedLine = previousePosition.y > lineY && currentPosition.y <= lineY;
        boolean isDesiredLine = currentPosition.x > lineStartX && currentPosition.x < lineEndX;

        return carCrossedLine && isDesiredLine;
    }

}
