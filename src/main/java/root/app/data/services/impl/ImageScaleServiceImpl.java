package root.app.data.services.impl;

import lombok.Getter;
import org.springframework.stereotype.Service;
import root.app.data.services.ImageScaleService;
import root.app.model.*;

import java.util.List;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toList;

@Service
public class ImageScaleServiceImpl implements ImageScaleService {

    @Override
    public List<MarkersPair> fixedSize(ScreenSize screenSize, List<MarkersPair> pairs) {
        return pairs.stream().peek(pair -> {
                    pair.setLineA(fixLinesScale.apply(screenSize, pair.getLineA()));
                    pair.setLineB(fixLinesScale.apply(screenSize, pair.getLineB()));
                }
        ).collect(toList());
    }

    @Override
    public Point fixScale(Point point, ScreenSize screenSizeInit, ScreenSize screenSize) {
        double heightMultiplier = screenSize.getWindowHeight() / screenSizeInit.getWindowHeight();
        double widthMultiplier = screenSize.getWindowWidth() / screenSizeInit.getWindowWidth();

        point.setX(point.getX() * widthMultiplier);
        point.setY(point.getY() * heightMultiplier);
        point.setWindowHeight(screenSize.getWindowHeight());
        point.setWindowWidth(screenSize.getWindowWidth());

        return point;
    }

    @Override
    public RoadWay.Zone fixedSize(ScreenSize screenSize, RoadWay.Zone zone) {

        final MarkersPair pair = zone.getPair();

        zone.setPair(fixPair(screenSize, pair));
        return zone;
    }

    @Override
    public PolygonDTO fixScale(ScreenSize screenSize, PolygonDTO ROI) {
        final ScreenSize screenSizeInit = new ScreenSize(ROI.getTopRight().getWindowHeight(), ROI.getTopRight().getWindowWidth());

        PolygonDTO roi = new PolygonDTO();

        roi.setTopRight(fixScale(ROI.getTopRight(), screenSizeInit, screenSize));
        roi.setTopLeft(fixScale(ROI.getTopLeft(), screenSizeInit, screenSize));
        roi.setBotRight(fixScale(ROI.getBotRight(), screenSizeInit, screenSize));
        roi.setBotLeft(fixScale(ROI.getBotLeft(), screenSizeInit, screenSize));
        roi.setDestination(ROI.getDestination());
        return roi;
    }

    private BiFunction<ScreenSize, Line, Line> fixLinesScale = (screenSize, line) -> {
        double heightMultiplier = screenSize.getWindowHeight() / line.getStart().getWindowHeight();
        double widthMultiplier = screenSize.getWindowWidth() / line.getStart().getWindowWidth();
        Point A1 = line.getStart();
        Point A2 = line.getEnd();

        A1.setX(A1.getX() * widthMultiplier);
        A1.setY(A1.getY() * heightMultiplier);
        A1.setWindowHeight(screenSize.getWindowHeight());
        A1.setWindowWidth(screenSize.getWindowWidth());

        A2.setX(A2.getX() * widthMultiplier);
        A2.setY(A2.getY() * heightMultiplier);
        A2.setWindowHeight(screenSize.getWindowHeight());
        A2.setWindowWidth(screenSize.getWindowWidth());

        line.setStart(A1);
        line.setEnd(A2);
        return line;
    };

    @Getter
    public static class ScreenSize {

        private double windowHeight;

        private double windowWidth;

        public ScreenSize(double windowHeight, double windowWidth) {
            this.windowHeight = windowHeight;
            this.windowWidth = windowWidth;
        }

    }

    private MarkersPair fixPair(ScreenSize screenSize, MarkersPair pair) {
        pair.setLineA(fixLinesScale.apply(screenSize, pair.getLineA()));
        pair.setLineB(fixLinesScale.apply(screenSize, pair.getLineB()));
        return pair;
    }
}
