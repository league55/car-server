package root.app.data.services;

import root.app.data.services.impl.ImageScaleServiceImpl;
import root.app.model.MarkersPair;
import root.app.model.Point;

import java.util.List;

public interface ImageScaleService {

    /**
     * Get all lines fixing size according to current image size
     * */
    List<MarkersPair> fixedSize(double currentHeight, double currentWidth, List<MarkersPair> pairs);

    Point fixScale(Point point, ImageScaleServiceImpl.ScreenSize screenSizeInit, ImageScaleServiceImpl.ScreenSize screenSize);
}
