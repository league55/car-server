package root.app.data.services.impl;

import org.opencv.core.Point;
import org.springframework.stereotype.Service;
import root.app.data.services.NextPositionPredictionService;

import java.util.List;

import static java.lang.Math.round;

/**
 * Predicting next car position to later match same car from 2 frames
 */
@Service
public class NextPositionPredictionServiceImpl implements NextPositionPredictionService {

    @Override
    public Point predictNextPosition(List<Point> centerPositions) {
        Point predictedNextPosition = new Point();


        int numPositions = centerPositions.size();
        Point lastCenter = centerPositions.get(numPositions - 1);

        if (numPositions == 1) {
            predictedNextPosition.x = lastCenter.x;
            predictedNextPosition.y = lastCenter.y;

        } else if (numPositions == 2) {

            double deltaX = centerPositions.get(1).x - centerPositions.get(0).x;
            double deltaY = centerPositions.get(1).y - centerPositions.get(0).y;

            predictedNextPosition.x = lastCenter.x + deltaX;
            predictedNextPosition.y = lastCenter.y + deltaY;

        } else if (numPositions == 3) {

            double sumOfXChanges = ((centerPositions.get(2).x - centerPositions.get(1).x) * 2) +
                    ((centerPositions.get(1).x - centerPositions.get(0).x) * 1);

            int deltaX = (int) Math.round((float) sumOfXChanges / 3.0);

            double sumOfYChanges = ((centerPositions.get(2).y - centerPositions.get(1).y) * 2) +
                    ((centerPositions.get(1).y - centerPositions.get(0).y) * 1);

            int deltaY = (int) round((float) sumOfYChanges / 3.0);

            predictedNextPosition.x = lastCenter.x + deltaX;
            predictedNextPosition.y = lastCenter.y + deltaY;

        } else if (numPositions == 4) {

            double sumOfXChanges = ((centerPositions.get(3).x - centerPositions.get(2).x) * 3) +
                    ((centerPositions.get(2).x - centerPositions.get(1).x) * 2) +
                    ((centerPositions.get(1).x - centerPositions.get(0).x) * 1);

            int deltaX = (int) round((float) sumOfXChanges / 6.0);

            double sumOfYChanges = ((centerPositions.get(3).y - centerPositions.get(2).y) * 3) +
                    ((centerPositions.get(2).y - centerPositions.get(1).y) * 2) +
                    ((centerPositions.get(1).y - centerPositions.get(0).y) * 1);

            int deltaY = (int) round((float) sumOfYChanges / 6.0);

            predictedNextPosition.x = lastCenter.x + deltaX;
            predictedNextPosition.y = lastCenter.y + deltaY;

        } else if (numPositions >= 5) {

            double sumOfXChanges = ((centerPositions.get(numPositions - 1).x - centerPositions.get(numPositions - 2).x) * 4) +
                    ((centerPositions.get(numPositions - 2).x - centerPositions.get(numPositions - 3).x) * 3) +
                    ((centerPositions.get(numPositions - 3).x - centerPositions.get(numPositions - 4).x) * 2) +
                    ((centerPositions.get(numPositions - 4).x - centerPositions.get(numPositions - 5).x) * 1);

            int deltaX = (int) round((float) sumOfXChanges / 10.0);

            double sumOfYChanges = ((centerPositions.get(numPositions - 1).y - centerPositions.get(numPositions - 2).y) * 4) +
                    ((centerPositions.get(numPositions - 2).y - centerPositions.get(numPositions - 3).y) * 3) +
                    ((centerPositions.get(numPositions - 3).y - centerPositions.get(numPositions - 4).y) * 2) +
                    ((centerPositions.get(numPositions - 4).y - centerPositions.get(numPositions - 5).y) * 1);

            int deltaY = (int) round((float) sumOfYChanges / 10.0);

            predictedNextPosition.x = lastCenter.x + deltaX;
            predictedNextPosition.y = lastCenter.y + deltaY;

        }
        return predictedNextPosition;

    }

}
