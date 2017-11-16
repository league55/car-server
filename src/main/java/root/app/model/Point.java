package root.app.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Point {

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
}

