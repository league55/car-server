package root.app.data.services;

import root.app.model.Line;
import root.app.model.Point;

public interface CalibrationService {

    void fixPosition(Integer n, Double deltaY);

    Point intersection(Line lineA, Line lineB);
}
