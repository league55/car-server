package root.app.data.services;

import org.opencv.core.Mat;
import root.app.data.services.impl.ImageScaleServiceImpl;
import root.app.model.Car;
import root.app.model.MarkersPair;

import java.util.List;

/**
 * Processing crossing lines
 */
public interface LineCrossingService {

    long countCars(List<Car> cars);

    void findCrossingLineCars(List<Car> cars, List<MarkersPair> lines);

    void findCrossingLineCars(List<Car> cars, ImageScaleServiceImpl.ScreenSize  screenSize);

}
