package root.app.data.services.impl;

import javafx.geometry.Bounds;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.app.data.services.ImageScaleService;
import root.app.data.services.LineCrossingService;
import root.app.data.services.ZoneComputingService;
import root.app.model.Car;
import root.app.model.Line;
import root.app.model.MarkersPair;
import root.app.model.RoadWay;
import root.app.properties.RoadWaysConfigService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LineCrossingServiceImpl implements LineCrossingService {

    private final RoadWaysConfigService zoneConfigService;
    private final ImageScaleService imageScaleService;
    private final ZoneComputingService zoneComputingService;

    @Autowired
    public LineCrossingServiceImpl(RoadWaysConfigService zoneConfigService, ImageScaleService imageScaleService, ZoneComputingService zoneComputingService) {
        this.zoneConfigService = zoneConfigService;
        this.imageScaleService = imageScaleService;
        this.zoneComputingService = zoneComputingService;
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

    public boolean isCarTakesZone(RoadWay.Zone zone, Car car) {
        final Rect boundingRect = car.currentBoundingRect;
        if (car.getCenterPositions().size() < 2 || !car.isStillTracked() || boundingRect == null) return false;

        final MarkersPair pair = zone.getPair();
        final Polygon zoneRect = new Polygon((zoneComputingService.getPolygonPoints(pair).stream().mapToDouble(i -> i).toArray()));
        final Rectangle carRect = new Rectangle(boundingRect.x, boundingRect.y, boundingRect.width, boundingRect.height);
        Bounds bounds = Shape.intersect(carRect, zoneRect).getBoundsInParent();

        final double intersectionArea = bounds.getHeight() * bounds.getWidth();
        final double zoneArea = zoneRect.getBoundsInParent().getHeight() * zoneRect.getBoundsInParent().getWidth();

        return (1 - (zoneArea - intersectionArea) / zoneArea > 0.3);
    }

    @Override
    public List<Car> setCrossingTimeMarks(List<Car> cars, ImageScaleServiceImpl.ScreenSize screenSize) {
        zoneConfigService.findAll().forEach(parentZone -> {
            //TODO: THIS BREAKS
            List<RoadWay.Zone> zones = parentZone.getZones().stream().map(childZone -> imageScaleService.fixedSize(screenSize, childZone)).collect(Collectors.toList());

            cars.forEach(car -> {
                findCrossedChildZone(zones, car, parentZone.getPair().getWayNum());
            });
        });

        return cars;
    }

    private void findCrossedChildZone(List<RoadWay.Zone> zones, Car car, Integer wayNum) {
        final Optional<RoadWay.Zone> crossedA = zones.stream().filter(chZ -> isCarCrossedLine(chZ.getPair().getLineA(), car)).findFirst();
        final Optional<RoadWay.Zone> crossedB = zones.stream().filter(chZ -> isCarCrossedLine(chZ.getPair().getLineB(), car)).findFirst();

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
            crossed.setWayNumber(wayNum);
            car.getCrossedPairs().add(crossed);
        });
    }

}
