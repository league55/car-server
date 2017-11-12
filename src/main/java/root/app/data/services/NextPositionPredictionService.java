package root.app.data.services;


import org.opencv.core.Point;

import java.util.List;

/**
 * Predicting next car position to later match same car from 2 frames
 */
public interface NextPositionPredictionService {

    Point predictNextPosition(List<Point> centerPositions);

}
