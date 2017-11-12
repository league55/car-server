package root.app.data.services;

import root.app.model.MarkersPair;

import java.util.List;

public interface ImageScaleService {

    /**
     * Get all lines fixing size according to current image size
     * */
    List<MarkersPair> fixedSize(double currentHeight, double currentWidth, List<MarkersPair> pairs);
}
