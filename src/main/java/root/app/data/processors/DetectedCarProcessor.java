package root.app.data.processors;

import org.opencv.core.Scalar;
import root.app.model.Car;

import java.util.List;

/**
 * Service for matching same cars from 2 neighbour frames
 */
public interface DetectedCarProcessor {
    Scalar SCALAR_BLACK = new Scalar(0.0, 0.0, 0.0);
    Scalar SCALAR_WHITE = new Scalar(255.0, 255.0, 255.0);
    Scalar SCALAR_YELLOW = new Scalar(0.0, 255.0, 255.0);
    Scalar SCALAR_GREEN = new Scalar(0.0, 200.0, 0.0);
    Scalar SCALAR_RED = new Scalar(0.0, 0.0, 255.0);

    void matchCurrentFrameDetectedCarsToExistingDetectedCars(List<Car> existingCars, List<Car> currentFrameCars);

}
