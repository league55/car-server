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
public class MarkersPair implements Cloneable, Serializable {

    private Long id = 0L;
    private Line lineA;
    private Line lineB;

    private Integer realDistance = 0;
    private Integer wayNum = 1;

    public MarkersPair(Line lineA, Line lineB) {
        this.lineA = lineA;
        this.lineB = lineB;
    }

    public MarkersPair(CalibrationDTO dto) {
        this.lineA = new Line(dto.getBotLeftAnchor(), dto.getBotRightAnchor());
        this.lineB = new Line(dto.getTopLeftAnchor(), dto.getTopRightAnchor());
    }

    @Override
    public MarkersPair clone() {
        return new MarkersPair(id, lineA.clone(), lineB.clone(), realDistance, wayNum);
    }
}
