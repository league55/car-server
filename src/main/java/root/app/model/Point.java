package root.app.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Point implements Cloneable {

    private Double x;
    private Double y;

    private Double windowHeight;
    private Double windowWidth;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y, double windowHeight, double windowWidth) {
        this.x = x;
        this.y = y;
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
    }

    public Point(org.opencv.core.Point lastCenter) {
        this.x = lastCenter.x;
        this.y = lastCenter.y;
    }

    @Override
    public Point clone() {
        return new Point(this.x, this.y, this.getWindowHeight(), this.getWindowWidth());
    }
}

