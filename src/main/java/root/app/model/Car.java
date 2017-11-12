package root.app.model;

import lombok.Data;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

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
    public List<Point> centerPositions;
    public double currentDiagonalSize;
    public double currentAspectRatio;
    public boolean isNewAppearCar;
    public boolean isStillTracked;
    public int numOfConsecutiveFramesWithoutAMatch;
    public Point predictedNextPosition;

    private Long secondMarkerCrossed;
    private Long fistMarkerCrossed;

    private int framesAfterFirstMarker;
    private int framesBetweenMarkers;

    private Integer distancePassed;

    private Double speed;

    public Car(MatOfPoint contour) {
        Point currentCenter = new Point();
        predictedNextPosition = new Point();
        centerPositions = new ArrayList<>();
        currentContour = contour;

        currentBoundingRect = Imgproc.boundingRect(currentContour);

        currentCenter.x = (currentBoundingRect.x + currentBoundingRect.x + currentBoundingRect.width) / 2;
        currentCenter.y = (currentBoundingRect.y + currentBoundingRect.y + currentBoundingRect.height) / 2;

        centerPositions.add(currentCenter);

        currentDiagonalSize = sqrt(pow(currentBoundingRect.width, 2) + pow(currentBoundingRect.height, 2));

        currentAspectRatio = (float) currentBoundingRect.width / (float) currentBoundingRect.height;
        numOfConsecutiveFramesWithoutAMatch = 0;
    }
}
