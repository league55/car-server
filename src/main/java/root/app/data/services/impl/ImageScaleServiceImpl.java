package root.app.data.services.impl;

import lombok.Getter;
import org.springframework.stereotype.Service;
import root.app.data.services.ImageScaleService;
import root.app.model.Line;
import root.app.model.MarkersPair;
import root.app.model.Point;

import java.util.List;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toList;

@Service
public class ImageScaleServiceImpl implements ImageScaleService {

    @Override
    public List<MarkersPair> fixedSize(double currentHeight, double currentWidth, List<MarkersPair> pairs) {
        return pairs.stream().peek(pair -> {
                    pair.setLineA(fixLinesScale.apply(new Point(currentWidth, currentHeight), pair.getLineA()));
                    pair.setLineB(fixLinesScale.apply(new Point(currentWidth, currentHeight), pair.getLineB()));
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

    private BiFunction<Point, Line, Line> fixLinesScale = (screenSize, line) -> {
        double heightMultiplier = screenSize.getY() / line.getStart().getWindowHeight();
        double widthMultiplier = screenSize.getX() / line.getStart().getWindowWidth();
        Point A1 = line.getStart();
        Point A2 = line.getEnd();

        A1.setX(A1.getX() * widthMultiplier);
        A1.setY(A1.getY() * heightMultiplier);
        A1.setWindowHeight(screenSize.getY());
        A1.setWindowWidth(screenSize.getX());

        A2.setX(A2.getX() * widthMultiplier);
        A2.setY(A2.getY() * heightMultiplier);
        A2.setWindowHeight(screenSize.getY());
        A2.setWindowWidth(screenSize.getX());

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
}
