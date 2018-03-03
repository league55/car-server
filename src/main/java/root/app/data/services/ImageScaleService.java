package root.app.data.services;

import root.app.data.services.impl.ImageScaleServiceImpl;
import root.app.model.MarkersPair;
import root.app.model.Point;
import root.app.model.PolygonDTO;
import root.app.model.RoadWay;

import java.util.List;

public interface ImageScaleService {

    /**
     * Get all lines fixing size according to current image size
     * */
    List<MarkersPair> fixedSize(ImageScaleServiceImpl.ScreenSize screenSize, List<MarkersPair> pairs);

    Point fixScale(Point point, ImageScaleServiceImpl.ScreenSize screenSizeInit, ImageScaleServiceImpl.ScreenSize screenSize);

    RoadWay.Zone fixedSize(ImageScaleServiceImpl.ScreenSize screenSize, RoadWay.Zone zone);

    PolygonDTO fixScale(ImageScaleServiceImpl.ScreenSize cvMatSize, PolygonDTO roi);

    MarkersPair fixPair(ImageScaleServiceImpl.ScreenSize screenSize, MarkersPair pair);
}
