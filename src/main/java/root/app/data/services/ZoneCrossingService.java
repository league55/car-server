package root.app.data.services;

import org.opencv.core.Mat;
import root.app.data.services.impl.ImageScaleServiceImpl;
import root.app.model.Car;
import root.app.model.MarkersPair;

import java.util.List;

/**
 * Processing crossing lines
 */
public interface ZoneCrossingService {

    long countCars(List<Car> cars);

    void paintBusyZones(List<Car> cars, ImageScaleServiceImpl.ScreenSize  screenSize);

}
