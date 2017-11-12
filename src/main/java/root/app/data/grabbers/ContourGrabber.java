package root.app.data.grabbers;

import org.opencv.core.Mat;
import root.app.model.Car;

import java.util.List;

public interface ContourGrabber {
    List<Car> getContours(Mat erodedMat, Mat hierarchy);
}
