package root.app.data.grabbers.impl;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import root.app.data.grabbers.ContourGrabber;
import root.app.model.Car;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.contourArea;

/**
 * Util to find contours
 */
@Service
public class ContourGrabberImpl implements ContourGrabber {

    @Override
    public List<Car> getContours(Mat erodedMat, Mat hierarchy, Point ofs) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(erodedMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<MatOfPoint> convexHulls = getConvexHull(contours);
        List<Car> currentFrameCars = new ArrayList<>();

        for (MatOfPoint convexHull : convexHulls) {
            Car car = new Car(convexHull, ofs);
            //TODO: config
            if (carSizeIsOK(car)) {
                currentFrameCars.add(car);
            }
        }

        return currentFrameCars;
    }

    private boolean carSizeIsOK(Car car) {
        return car.currentBoundingRect.area() > 400 &&
                car.currentAspectRatio > 0.2 &&
                car.currentAspectRatio < 4.0 &&
                car.currentBoundingRect.width > 30 &&
                car.currentBoundingRect.height > 30 &&
                car.currentDiagonalSize > 100.0 &&
                (contourArea(car.currentContour) / car.currentBoundingRect.area()) > 0.50;
    }

    private static List<MatOfPoint> getConvexHull(List<MatOfPoint> contours) {
        // Find the convex hull
        List<MatOfInt> hull = new ArrayList<MatOfInt>();
        for (int i = 0; i < contours.size(); i++) {
            hull.add(new MatOfInt());
        }
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.convexHull(contours.get(i), hull.get(i));
        }

        // Convert MatOfInt to MatOfPoint for drawing convex hull

        // Loop over all contours
        List<Point[]> hullpoints = new ArrayList<Point[]>();
        for (int i = 0; i < hull.size(); i++) {
            Point[] points = new Point[hull.get(i).rows()];

            // Loop over all points that need to be hulled in current contour
            for (int j = 0; j < hull.get(i).rows(); j++) {
                int index = (int) hull.get(i).get(j, 0)[0];
                points[j] = new Point(contours.get(i).get(index, 0)[0], contours.get(i).get(index, 0)[1]);
            }

            hullpoints.add(points);
        }

        // Convert Point arrays into MatOfPoint
        List<MatOfPoint> hullmop = new ArrayList<MatOfPoint>();
        for (Point[] hullpoint : hullpoints) {
            MatOfPoint mop = new MatOfPoint();
            mop.fromArray(hullpoint);
            hullmop.add(mop);
        }
        return hullmop;
    }

}
