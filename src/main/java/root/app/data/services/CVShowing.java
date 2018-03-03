package root.app.data.services;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import root.app.data.services.impl.ImageScaleServiceImpl;
import root.app.model.Car;

import java.util.List;

/**
 * Drawing any info on open cv frames
 */
public interface CVShowing {
    void drawAndShowContours(Mat frame, Size imageSize, List<Car> contours);

    void drawCarInfoOnImage(List<Car> cars, Mat imgFrame2Copy);

    void drawCarCountOnImage(long carCount, Mat imgFrame2Copy);

    void drawZones(Mat imgFrame2Copy, ImageScaleServiceImpl.ScreenSize cvMatSize, List<Car> cars);

    void drawRect(Mat frame2, Rect roi);
}
