package root.app.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Used for counting speed
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkersPair extends BasicModel {
    private Long id;

    private Line lineA;
    private Line lineB;

    private Integer distanceLeft = 0;
//    private Integer distanceRight = 0;
    private Integer wayNum = 0;

    public MarkersPair(long id, Line lineA, Line lineB) {
        this.id = id;
        this.lineA = lineA;
        this.lineB = lineB;
    }
}
