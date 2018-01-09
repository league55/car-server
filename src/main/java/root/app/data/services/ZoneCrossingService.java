package root.app.data.services;

import javafx.scene.layout.Pane;
import root.app.model.Car;

import java.util.List;

/**
 * Processing crossing lines
 */
public interface ZoneCrossingService {

    long countCars(List<Car> cars);

    void paintBusyZones(List<Car> cars, Pane zonesContainer);

}
