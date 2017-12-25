package root.app.data.services;

import javafx.collections.transformation.FilteredList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import root.app.controllers.MainController;
import root.app.model.Car;
import root.app.model.MarkersPair;
import root.app.model.Point;
import root.app.model.Zone;
import root.app.properties.ConfigService;

import java.util.List;
import java.util.function.Predicate;

import static root.app.data.services.impl.ImageScaleServiceImpl.ScreenSize;

@Component
@Qualifier("zoneCrossingService")
@Slf4j
public class ZoneCrossingService implements LineCrossingService {

    @Autowired
    private ConfigService<Zone> zoneConfigService;
    @Autowired
    private ZoneComputingService zoneComputingService;

    @Autowired
    private ImageScaleService scaleService;

    @Autowired
    private MainController mainController;

    @Override
    public long countCars(List<Car> cars) {
        return cars.stream()
                .filter(car -> car.getFistMarkerCrossed() != null && car.getSecondMarkerCrossed() != null).count();

    }

    @Override
    public void findCrossingLineCars(List<Car> cars, List<MarkersPair> lines) {

    }

    @Override
    public void findCrossingLineCars(List<Car> cars, ScreenSize screenSize) {
        final List<Zone> parZones = zoneConfigService.findAll();

        final Bounds boundsInLocal = mainController.getImageView().getBoundsInLocal();

        final AnchorPane zonesContainer = mainController.getImageWrapperPane();
        parZones.forEach(parZone -> {
            final List<Zone.ChildZone> childZones = parZone.getChildZones();
            for (int i = 0, childZonesSize = childZones.size(); i < childZonesSize; i++) {
                final int in = i;

                Zone.ChildZone child = childZones.get(i);

                final Predicate<Car> carInZone = car -> {
                    //different image size when detect and display
                    Point fixedScalePoint = scaleService.fixScale(new Point(car.getLastCenter()), screenSize, new ScreenSize(boundsInLocal.getHeight(), boundsInLocal.getWidth()));

                    try {
                        return zoneComputingService.toFxPolygon(child, parZone, in).getBoundsInParent().contains(fixedScalePoint.getX(), fixedScalePoint.getY());
                    } catch (Exception e) {
                        log.error("error", e);
                        return false;
                    }
                };

                final FilteredList<Node> polygon = zonesContainer.getChildren().filtered(node -> zoneComputingService.getChildZoneId(parZone, in).equals(node.getId()));


                if (cars.stream().filter(car -> car.isStillTracked).anyMatch(carInZone)) {
                    polygon.forEach(p -> ((Polygon) p).setFill(Color.GREEN));
                } else {
                    polygon.forEach(p -> ((Polygon) p).setFill(Color.RED));
                }
            }
        });
        ;

    }
}
