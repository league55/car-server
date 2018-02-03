package root.app.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Base dto which represents two pairs of lines, distance between them
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkersPair extends BasicModel implements Cloneable, Serializable {

    private Line lineA;
    private Line lineB;

    private Integer distanceLeft = 0;
    //    private Integer distanceRight = 0;
    private Integer wayNum = 1;

    public MarkersPair(Line lineA, Line lineB) {
        this.lineA = lineA;
        this.lineB = lineB;
    }

    public MarkersPair clone() {
        final MarkersPair markersPair = new MarkersPair(
                new Line(new Point(this.getLineA().getStart().getX(), this.getLineA().getStart().getY(), this.getLineA().getStart().getWindowHeight(), this.getLineA().getStart().getWindowWidth()),
                        new Point(this.getLineA().getEnd().getX(), this.getLineA().getEnd().getY(), this.getLineA().getEnd().getWindowHeight(), this.getLineA().getEnd().getWindowWidth())),
                new Line(new Point(this.getLineB().getStart().getX(), this.getLineB().getStart().getY(), this.getLineB().getStart().getWindowHeight(), this.getLineB().getStart().getWindowWidth()),
                        new Point(this.getLineB().getEnd().getX(), this.getLineB().getEnd().getY(), this.getLineB().getEnd().getWindowHeight(), this.getLineB().getEnd().getWindowWidth())),
                this.getDistanceLeft(),
                this.getWayNum());
        markersPair.setId(this.getId());
        return markersPair;
    }
}
