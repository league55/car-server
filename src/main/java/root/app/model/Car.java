package root.app.model;

import lombok.Data;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import root.app.data.services.impl.ImageScaleServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * DTO represents frame over car;
 * (don't call it frame because of similar video frames in app.controllers)
 */
@Data
public class Car {

    public MatOfPoint currentContour;
    public Rect currentBoundingRect;
    public double currentDiagonalSize;
    public double currentAspectRatio;
    public boolean isNewAppearCar;
    public boolean isStillTracked;
    public int numOfConsecutiveFramesWithoutAMatch;
    public Point predictedNextPosition;

    private Long secondMarkerCrossed;
    private Long fistMarkerCrossed;

    private ImageScaleServiceImpl.ScreenSize screenSize;

    private MarkersPair passedPair;

    private Double speed;

    private Point lastCenter;
    public List<Point> centerPositions;

    public Car(MatOfPoint contour) {
        Point currentCenter = new Point();
        predictedNextPosition = new Point();
        centerPositions = new ArrayList<>();
        currentContour = contour;

        currentBoundingRect = Imgproc.boundingRect(currentContour);

        currentCenter.x = (currentBoundingRect.x + currentBoundingRect.x + currentBoundingRect.width) / 2;
        currentCenter.y = (currentBoundingRect.y + currentBoundingRect.y + currentBoundingRect.height) / 2;

        lastCenter = currentCenter;
        centerPositions.add(currentCenter);

        currentDiagonalSize = sqrt(pow(currentBoundingRect.width, 2) + pow(currentBoundingRect.height, 2));

        currentAspectRatio = (float) currentBoundingRect.width / (float) currentBoundingRect.height;
        numOfConsecutiveFramesWithoutAMatch = 0;
    }
}
