package root.app.data.detectors.impl;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import root.app.data.detectors.Detector;
import root.app.model.Car;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.contourArea;

/**
 * Created by maksym on 24.09.17.
 */
public class HaarDetectorImpl implements Detector {
    private CascadeClassifier carsCascade;

    private HaarDetectorImpl() {
        carsCascade = new CascadeClassifier();
        this.carsCascade.load("C:\\Users\\maksym\\IdeaProjects\\opencv-samples\\vehicle-detection-haar\\cars3.xml");
    }

    private int absoluteCarsSize;

    @Override
    public List<Car> detectCars(Mat frame, Mat frame2) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteCarsSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteCarsSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        this.carsCascade.detectMultiScale(grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteCarsSize, this.absoluteCarsSize), new Size());

//        List<MatOfPoint> cars = faces.toList().stream().map(rect -> {
//            Mat mat = new Mat();
//            rectangle(mat, rect.tl(), rect.br(), new Scalar(0, 255, 0), 3);
//            return new MatOfPoint(mat);
//        }).collect(toList());
        // each rectangle in faces is a face: draw them!
        Rect[] carsArray = faces.toArray();
        List<Car> currentFrameCars = new ArrayList<>();
        MatOfPoint contour2f = new MatOfPoint();
        ;List<MatOfPoint> cars = new ArrayList<>();

        for (MatOfPoint convexHull : cars) {
            Car car = new Car(convexHull);

            if (car.currentBoundingRect.area() > 400 &&
                    car.currentAspectRatio > 0.2 &&
                    car.currentAspectRatio < 4.0 &&
                    car.currentBoundingRect.width > 30 &&
                    car.currentBoundingRect.height > 30 &&
                    car.currentDiagonalSize > 100.0 &&
                    (contourArea(car.currentContour) / car.currentBoundingRect.area()) > 0.50) {
                currentFrameCars.add(car);
            }
        }
        for (Rect aCarArray : carsArray)
            Imgproc.rectangle(frame2, aCarArray.tl(), aCarArray.br(), new Scalar(0, 255, 0), 3);

        return currentFrameCars;
    }

}
