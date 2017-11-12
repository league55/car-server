package root.app.data.detectors.impl;

import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.detectors.Detector;
import root.app.data.grabbers.BackgroundSubstitutor;
import root.app.data.grabbers.ContourGrabber;
import root.app.model.Car;

import java.util.List;

/**
 * Based on finding moving parts comparing 2 frames
 */
@Service
public class SubstitutionDetectorImpl implements Detector {

    final
    ContourGrabber contourGrabber;

    @Autowired
    public SubstitutionDetectorImpl(ContourGrabber contourGrabber) {
        this.contourGrabber = contourGrabber;
    }

    @Override
    public List<Car> detectCars(Mat frame, Mat frame2) {
        Mat erodedMat = new Mat();

        BackgroundSubstitutor.getObjects(frame, frame2, erodedMat);

        Mat hierarchy = new Mat();

       return contourGrabber.getContours(erodedMat, hierarchy);
    }

}
