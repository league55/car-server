package root.app.data.services.impl;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import root.app.data.services.CVShowing;
import root.app.model.Car;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;
import static org.opencv.imgproc.Imgproc.getTextSize;
import static org.opencv.imgproc.Imgproc.putText;
import static root.app.data.processors.DetectedCarProcessor.SCALAR_GREEN;
import static root.app.data.processors.DetectedCarProcessor.SCALAR_WHITE;


@Service
public class CVShowingImpl implements CVShowing {
    private static final int CV_FONT_HERSHEY_SIMPLEX = 1;

    @Override
    public void drawCarInfoOnImage(List<Car> cars, Mat imgFrame2Copy) {
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);

            if (car.isStillTracked) {
                Rect currentBoundingRect = car.currentBoundingRect;
                Imgproc.rectangle(imgFrame2Copy, currentBoundingRect.tl(), currentBoundingRect.br(), new Scalar(0, 255, 0), 3);

                double dblFontScale = car.currentDiagonalSize / 60.0;
                int intFontThickness = (int) round(dblFontScale * 1.0);
                List<Point> centerPositions = car.centerPositions;
//              putText(imgFrame2Copy, i + "", centerPositions.get(centerPositions.size() - 1), CV_FONT_HERSHEY_SIMPLEX, dblFontScale, SCALAR_GREEN, intFontThickness);

                if (car.getSpeed() != null) {
                    putText(imgFrame2Copy, car.getSpeed() + "km/h", centerPositions.get(centerPositions.size() - 1), CV_FONT_HERSHEY_SIMPLEX, dblFontScale, SCALAR_GREEN, intFontThickness);
                }
            }
        }
    }

    @Override
    public void drawCarCountOnImage(long carCount, Mat imgFrame2Copy) {
        int intFontFace = CV_FONT_HERSHEY_SIMPLEX;
        double dblFontScale = (imgFrame2Copy.rows() * imgFrame2Copy.cols()) / 300000.0;
        int intFontThickness = (int) round(dblFontScale * 1.5);

        Size textSize = getTextSize(carCount + "", intFontFace, dblFontScale, intFontThickness, null);

        Point ptTextBottomLeftPosition = new Point();

        ptTextBottomLeftPosition.x = imgFrame2Copy.cols() - 1 - (int) (textSize.width * 1.25);
        ptTextBottomLeftPosition.y = (int) (textSize.height * 1.25);

        putText(imgFrame2Copy, carCount + "", ptTextBottomLeftPosition, intFontFace, dblFontScale, SCALAR_GREEN, intFontThickness);

    }


    @Override
    public void drawAndShowContours(Mat frame, Size imageSize, List<Car> cars) {

        List<MatOfPoint> contours = new ArrayList<>();

        for (Car car : cars) {
            if (car.isStillTracked) {
                contours.add(car.currentContour);
            }
        }

        Imgproc.drawContours(frame, contours, -1, SCALAR_WHITE, -1);
    }
}
