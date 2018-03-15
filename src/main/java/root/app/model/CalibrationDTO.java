package root.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CalibrationDTO {

    private Point topLeftAnchor;
    private Point topRightAnchor;
    private Point botLeftAnchor;
    private Point botRightAnchor;
}
