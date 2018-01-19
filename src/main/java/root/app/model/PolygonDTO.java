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
public class PolygonDTO extends BasicModel {

    private Point topLeft;
    private Point topRight;
    private Point botRight;
    private Point botLeft;

    private Destination destination;

    public enum Destination {
        ROI, PerspectiveTransform
    }
}
