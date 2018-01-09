package root.app.data.services.impl;

import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import root.app.data.services.ZoneCrossingService;
import root.app.model.Car;
import root.app.model.CrossedPair;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("zoneCrossingService")
@Slf4j
public class ZoneCrossingServiceImpl implements ZoneCrossingService {

    @Override
    public long countCars(List<Car> cars) {
        return cars.stream()
                .map(Car::getCrossedPairs)
                .flatMap(Collection::stream)
                .filter(crossedPair -> crossedPair.getTimeLeavedZone() != null)
                .count();
    }

    @Override
    public void paintBusyZones(List<Car> cars, Pane zonesContainer) {

        final List<String> childZonesWithCars = cars.stream()
                .map(Car::getCrossedPairs)
                .flatMap(Collection::stream)
                .filter(cz -> cz.getTimeEnteredZone() != null && cz.getTimeLeavedZone() == null)
                .map(CrossedPair::getZoneId)
                .collect(Collectors.toList());

        final List<String> childZonesWithoutCars = cars.stream()
                .map(Car::getCrossedPairs)
                .flatMap(Collection::stream)
                .filter(cz -> cz.getTimeEnteredZone() != null && cz.getTimeLeavedZone() != null)
                .map(CrossedPair::getZoneId)
                .collect(Collectors.toList());

        childZonesWithCars.forEach(id -> {
            final FilteredList<Node> polygon = zonesContainer.getChildren().filtered(node -> id.equals(node.getId()));
            polygon.forEach(p -> ((Polygon) p).setFill(Color.GREEN));
        });


        childZonesWithoutCars.forEach(id -> {
            final FilteredList<Node> polygon = zonesContainer.getChildren().filtered(node -> id.equals(node.getId()));
            polygon.forEach(p -> ((Polygon) p).setFill(Color.RED));
        });

    }
}
