package root.app.data.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.app.data.services.LineCrossingService;
import root.app.model.Car;
import root.app.model.Line;
import root.app.model.Zone;
import root.app.properties.impl.ZoneConfigService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LineCrossingServiceImpl implements LineCrossingService {

    private final ZoneConfigService zoneConfigService;

    @Autowired
    public LineCrossingServiceImpl(ZoneConfigService zoneConfigService) {
        this.zoneConfigService = zoneConfigService;
    }

    @Override
    public boolean isCarCrossedAllZones(Car car) {
        return true;

    }

    @Override
    public boolean isCarCrossedLine(Line line, Car car) {
        if (car.getCenterPositions().size() < 2 || !car.isStillTracked()) return false;

        int prevFrameIndex = car.centerPositions.size() - 2;
        int currFrameIndex = car.centerPositions.size() - 1;

        double lineY = line.getEnd().getY();
        double lineStartX = line.getStart().getX();
        double lineEndX = line.getEnd().getX();

        Point previousPosition = car.centerPositions.get(prevFrameIndex);
        Point currentPosition = car.centerPositions.get(currFrameIndex);

        boolean carCrossedLine = previousPosition.y > lineY && currentPosition.y <= lineY;
        boolean isDesiredLine = currentPosition.x > lineStartX && currentPosition.x < lineEndX;

        return carCrossedLine && isDesiredLine;
    }

    @Override
    public List<Car> setCrossingTimeMarks(List<Car> cars) {
        List<Zone.ChildZone> childZones = zoneConfigService.findAll().stream().map(Zone::getChildZones).flatMap(Collection::stream).collect(Collectors.toList());

        cars.forEach(car -> {
            findCrossedChildZone(childZones, car);
        });

        return cars;
    }

    private void findCrossedChildZone(List<Zone.ChildZone> childZones, Car car) {
        final Optional<Zone.ChildZone> crossedA = childZones.stream().filter(chZ -> isCarCrossedLine(chZ.getPair().getLineA(), car)).findFirst();
        final Optional<Zone.ChildZone> crossedB = childZones.stream().filter(chZ -> isCarCrossedLine(chZ.getPair().getLineB(), car)).findFirst();

        crossedB.ifPresent(childZone -> {
            try {
                car.getCrossedPairs().get(car.getCrossedPairs().size() - 1).setTimeLeavedZone(System.currentTimeMillis());
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("ArrayIndexOutOfBoundsException", e);
                //This car was lost and found back. Can't count speed but can take into account when count total taken zones.
            }
            if (childZone.getIsLast()) {
                car.setStillTracked(false);
            }
        });

        crossedA.ifPresent(childZone -> {
            final root.app.model.CrossedPair crossed = new root.app.model.CrossedPair();
            crossed.setTimeEnteredZone(System.currentTimeMillis());
            crossed.setZoneId(childZone.getId());
            car.getCrossedPairs().add(crossed);
        });

    }

}
