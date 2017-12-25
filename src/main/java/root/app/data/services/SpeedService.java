package root.app.data.services;

import root.app.model.Car;

import java.util.List;

/**
 * Service for counting speed of cars
 */
public interface SpeedService {

   void countSpeed(List<Car> cars);
}
