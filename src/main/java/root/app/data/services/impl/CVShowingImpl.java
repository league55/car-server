package root.app.data.services.impl;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.services.CVShowing;
import root.app.data.services.ZoneComputingService;
import root.app.model.Car;
import root.app.model.MarkersPair;
import root.app.model.RoadWay;
import root.app.properties.RoadWaysConfigService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Math.round;
import static org.opencv.imgproc.Imgproc.getTextSize;
import static org.opencv.imgproc.Imgproc.putText;
import static root.app.data.processors.DetectedCarProcessor.*;


@Service
public class CVShowingImpl implements CVShowing {
    private static final int CV_FONT_HERSHEY_SIMPLEX = 1;

    private final RoadWaysConfigService waysConfigService;
    private final ZoneComputingService zoneComputingService;
    private final ImageScaleServiceImpl scaleService;

    @Autowired
    public CVShowingImpl(RoadWaysConfigService waysConfigService, ZoneComputingService zoneComputingService, ImageScaleServiceImpl scaleService) {
        this.waysConfigService = waysConfigService;
        this.zoneComputingService = zoneComputingService;
        this.scaleService = scaleService;
    }


    @Override
    public void drawCarInfoOnImage(List<Car> cars, Mat frame) {
        for (Car car : cars) {
            if (car.isStillTracked) {
                Rect currentBoundingRect = car.currentBoundingRect;
                Imgproc.rectangle(frame, currentBoundingRect.tl(), currentBoundingRect.br(), new Scalar(0, 255, 0), 3);

                double dblFontScale = car.currentDiagonalSize / 60.0;
                int intFontThickness = (int) round(dblFontScale * 1.0);
                List<Point> centerPositions = car.centerPositions;
//              putText(frame, i + "", centerPositions.get(centerPositions.size() - 1), CV_FONT_HERSHEY_SIMPLEX, dblFontScale, SCALAR_GREEN, intFontThickness);

                putText(frame, "", centerPositions.get(centerPositions.size() - 1), CV_FONT_HERSHEY_SIMPLEX, dblFontScale, SCALAR_GREEN, intFontThickness);
                if (car.getCrossedPairs().size() > 0) {
                    Double lastSpeed = car.getCrossedPairs().get(car.getCrossedPairs().size() - 1).getSpeed();
                    if (lastSpeed == null) lastSpeed = 0.0;
//                    putText(frame, lastSpeed + "km/h", centerPositions.get(centerPositions.size() - 1), CV_FONT_HERSHEY_SIMPLEX, dblFontScale, SCALAR_GREEN, intFontThickness);
                }
            }
        }
    }

    @Override
    public void drawCarCountOnImage(long carCount, Mat imgFrame2Copy) {
        int intFontFace = CV_FONT_HERSHEY_SIMPLEX;
        double dblFontScale = (imgFrame2Copy.rows() * imgFrame2Copy.cols()) / 300000.0;
        int intFontThickness = (int) round(dblFontScale * 1.5);

        Size textSize = getTextSize(carCount + "", intFontFace, dblFontScale, intFontThickness, null);

        Point ptTextBottomLeftPosition = new Point();

        ptTextBottomLeftPosition.x = imgFrame2Copy.cols() - 1 - (int) (textSize.width * 1.25);
        ptTextBottomLeftPosition.y = (int) (textSize.height * 1.25);

        putText(imgFrame2Copy, carCount + "", ptTextBottomLeftPosition, intFontFace, dblFontScale, SCALAR_GREEN, intFontThickness);

    }

    @Override
    public void drawZones(Mat imgFrame2Copy, ImageScaleServiceImpl.ScreenSize cvMatSize, List<Car> cars) {
        final List<RoadWay.Zone> zones = waysConfigService.findAllZones();
        final List<String> busyZones = cars.stream().map(car -> {
            if (car.getCrossedPairs().size() > 0) {
                return car.getCrossedPairs().get(car.getCrossedPairs().size() - 1).getZoneId();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        final Mat zonesMat = imgFrame2Copy.clone();

        zones.forEach(zone -> {
            final MarkersPair fixedPair = scaleService.fixPair(cvMatSize, zone.getPair());
            Imgproc.fillConvexPoly(zonesMat, new MatOfPoint(zoneComputingService.toCvPoint(fixedPair)), busyZones.contains(zone.getId()) ? SCALAR_GREEN : SCALAR_RED);
        });

        Core.addWeighted(zonesMat, 0.1, imgFrame2Copy, 0.9, 0, imgFrame2Copy);
        zones.forEach(roadWay -> {
            final MarkersPair markersPair = scaleService.fixPair(cvMatSize, roadWay.getPair());
            Imgproc.line(
                    imgFrame2Copy,
                    new Point(markersPair.getLineA().getStart().getX(), markersPair.getLineA().getStart().getY()),
                    new Point(markersPair.getLineB().getStart().getX(), markersPair.getLineB().getStart().getY()),
                    SCALAR_BLACK);
            Imgproc.line(
                    imgFrame2Copy,
                    new Point(markersPair.getLineA().getStart().getX(), markersPair.getLineA().getStart().getY()),
                    new Point(markersPair.getLineA().getEnd().getX(), markersPair.getLineA().getEnd().getY()),
                    SCALAR_BLACK);
        });
    }

    @Override
    public void drawRect(Mat frame, Rect roi) {
        if (roi == null) return;
        Imgproc.rectangle(frame, roi.tl(), roi.br(), new Scalar(57, 57, 57), 2);
    }


    @Override
    public void drawAndShowContours(Mat frame, Size imageSize, List<Car> cars) {

        List<MatOfPoint> contours = new ArrayList<>();

        for (Car car : cars) {
            if (car.isStillTracked) {
                contours.add(car.currentContour);
            }
        }

        Imgproc.drawContours(frame, contours, -1, SCALAR_WHITE, -1);
    }
}
