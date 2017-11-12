package root.app.data.detectors;

import org.opencv.core.Mat;
import root.app.model.Car;

import java.util.List;

/**
 * Find cars on image
 */
public interface Detector {

    List<Car> detectCars(Mat frame, Mat frame2);

}
