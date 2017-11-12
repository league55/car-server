package root.app.data.services;

import root.app.model.Car;
import root.app.model.MarkersPair;

import java.util.List;

/**
 * Processing crossing lines
 */
public interface LineCrossingService {

    long countCars(List<Car> cars);

    void findCrossingLineCars(List<Car> cars, List<MarkersPair> lines);

}
