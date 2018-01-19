package root.app.model;

import com.google.common.collect.Lists;
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
    private boolean wasCounted;

    private ImageScaleServiceImpl.ScreenSize screenSize;
    private List<CrossedPair> crossedPairs = Lists.newArrayList();

    private Point lastCenter;
    public List<Point> centerPositions;

    public Car(MatOfPoint contour, Point ofs) {
        Point currentCenter = new Point();
        predictedNextPosition = new Point();
        centerPositions = new ArrayList<>();
        currentContour = contour;
        numOfConsecutiveFramesWithoutAMatch = 0;

        currentBoundingRect = Imgproc.boundingRect(currentContour);
        currentBoundingRect = new Rect(currentBoundingRect.x + (int) ofs.x, currentBoundingRect.y + (int) ofs.y, currentBoundingRect.width, currentBoundingRect.height);

        currentCenter.x = (currentBoundingRect.x + currentBoundingRect.x + currentBoundingRect.width) / 2;
        currentCenter.y = (currentBoundingRect.y + currentBoundingRect.y + currentBoundingRect.height) / 2;

        lastCenter = currentCenter;
        centerPositions.add(currentCenter);

        currentDiagonalSize = sqrt(pow(currentBoundingRect.width, 2) + pow(currentBoundingRect.height, 2));

        currentAspectRatio = (float) currentBoundingRect.width / (float) currentBoundingRect.height;
    }
}
