package root.app.data.services;

import root.app.model.Line;
import root.app.model.Point;

public interface CalibrationService {

    void fixPosition(Integer n, Double deltaY);

    boolean canCalibrate(String calibrationType);

    static Point intersection(Line lineA, Line lineB) {
        Point A = lineA.getStart();
        Point B = lineA.getEnd();
        Point C = lineB.getStart();
        Point D = lineB.getEnd();

        double denominator = (A.getX() - B.getX()) * (C.getY() - D.getY()) - (A.getY() - B.getY()) * (C.getX() - D.getX());

        if (denominator != 0) {
            double px = ((A.getX() * B.getY() - A.getY() * B.getX()) * (C.getX() - D.getX()) - (A.getX() - B.getX())
                    * (C.getX() * D.getY() - C.getY() * D.getX()))
                    / denominator;
            double py = ((A.getX() * B.getY() - A.getY() * B.getX()) * (C.getY() - D.getY()) - (A.getY() - B.getY())
                    * (C.getX() * D.getY() - C.getY() * D.getX()))
                    / denominator;

            return new Point(px, py, A.getWindowHeight(), A.getWindowWidth());

        }

        return null;
    }
}
