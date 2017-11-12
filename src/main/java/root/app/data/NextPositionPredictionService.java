package root.app.data;

import org.opencv.core.Point;

import java.util.List;

/**
 * TODO: move implementation from Cars.java
 */
public interface NextPositionPredictionService {

    Point predict(List<Point> centerPositions);
}
