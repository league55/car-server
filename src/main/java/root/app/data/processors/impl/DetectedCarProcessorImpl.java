package root.app.data.processors.impl;

import org.opencv.core.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.processors.DetectedCarProcessor;
import root.app.data.services.ImageScaleService;
import root.app.data.services.NextPositionPredictionService;
import root.app.model.Car;

import java.util.List;

import static java.lang.Math.*;

@Service
public class DetectedCarProcessorImpl implements DetectedCarProcessor {

    private static final int REMEMBER_FRAMES = 15;

    @Autowired
    private NextPositionPredictionService predictionService;
    @Autowired
    private ImageScaleService scaleService;

    @Override
    public void matchCurrentFrameDetectedCarsToExistingDetectedCars(List<Car> existingCars, List<Car> currentFrameCars) {
        for (Car existingCar : existingCars) {

            existingCar.isNewAppearCar = false;

            existingCar.predictedNextPosition = predictionService.predictNextPosition(existingCar.centerPositions);
        }

        for (Car currentFrameCar : currentFrameCars) {

            int intIndexOfLeastDistance = 0;
            double leastDistance = 200000.0;

            for (int i = 0; i < existingCars.size(); i++) {
                if (existingCars.get(i).isStillTracked) {
                    List<Point> centerPositions = currentFrameCar.centerPositions;
                    double distance = distanceBetweenPoints(centerPositions.get(centerPositions.size() - 1), existingCars.get(i).predictedNextPosition);

                    if (distance < leastDistance) {
                        leastDistance = distance;
                        intIndexOfLeastDistance = i;
                    }
                }
            }

            if (leastDistance < currentFrameCar.currentDiagonalSize * 0.5) {
                addDetectedCarToExistingDetectedCars(currentFrameCar, existingCars, intIndexOfLeastDistance);
            } else {
                addNewDetectedCar(currentFrameCar, existingCars);
            }

        }

        for (Car existingCar : existingCars) {

            if (!existingCar.isNewAppearCar) {
                existingCar.numOfConsecutiveFramesWithoutAMatch++;
            }

            if (existingCar.numOfConsecutiveFramesWithoutAMatch >= REMEMBER_FRAMES) {
                existingCar.isStillTracked = false;
            }

        }

    }


    private void addDetectedCarToExistingDetectedCars(Car currentFrameCar, List<Car> existingCars, int intIndex) {

        Car car = existingCars.get(intIndex);
        Point lastCenter = currentFrameCar.centerPositions.get(currentFrameCar.centerPositions.size() - 1);
        car.currentContour = currentFrameCar.currentContour;
        car.currentBoundingRect = currentFrameCar.currentBoundingRect;

        car.centerPositions.add(lastCenter);
        car.setLastCenter(lastCenter);

        car.currentDiagonalSize = currentFrameCar.currentDiagonalSize;
        car.currentAspectRatio = currentFrameCar.currentAspectRatio;

        car.isStillTracked = true;
        car.isNewAppearCar = true;
    }

    private void addNewDetectedCar(Car currentFrameCar, List<Car> existingCars) {
        currentFrameCar.isNewAppearCar = true;
        currentFrameCar.isStillTracked = true;

        existingCars.add(currentFrameCar);
    }


    private double distanceBetweenPoints(Point point1, Point point2) {

        double intX = abs(point1.x - point2.x);
        double intY = abs(point1.y - point2.y);

        return (sqrt(pow(intX, 2) + pow(intY, 2)));
    }

}
