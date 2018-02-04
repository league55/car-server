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

    private Integer realDistance = 0;
    private Integer wayNum = 1;

    public MarkersPair(Line lineA, Line lineB) {
        this.lineA = lineA;
        this.lineB = lineB;
    }
}
