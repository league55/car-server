package root.app.data.services;

import root.app.model.Car;
import root.app.model.Line;
import root.app.model.MarkersPair;
import root.app.model.Zone;

import java.util.List;

public interface LineCrossingService {

    boolean isCarCrossedAllZones(Car car);

    boolean isCarCrossedLine(Line line, Car car);

    List<Car> setCrossingTimeMarks(List<Car> cars);
}
