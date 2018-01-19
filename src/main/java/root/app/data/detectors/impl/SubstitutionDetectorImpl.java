package root.app.data.detectors.impl;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
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

    private final ContourGrabber contourGrabber;

    @Autowired
    public SubstitutionDetectorImpl(ContourGrabber contourGrabber) {
        this.contourGrabber = contourGrabber;
    }

    @Override
    public List<Car> detectCars(Mat frame, Mat frame2, Rect roi) {
        Mat copy = frame;
        Mat copy2 = frame2;
        Mat finalMat = new Mat();
        Point ofs = new Point();

        if (roi != null) {
            copy = new Mat(frame, roi);
            copy2 = new Mat(frame2, roi);
            finalMat = new Mat(frame2.clone(), roi);
            ofs = new Point(roi.x, roi.y);
        }

        return detectCars(copy, copy2, finalMat, ofs);
    }


    private List<Car> detectCars(Mat frame, Mat frame2, Mat finalMat, Point ofs) {
        BackgroundSubstitutor.getObjects(frame, frame2, finalMat);
        Mat hierarchy = new Mat();
        return contourGrabber.getContours(finalMat, hierarchy, ofs);
    }
}
