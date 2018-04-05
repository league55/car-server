package root.app.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GridUpdateRequest {
    private String calibrationType;
    private Double value;
    private Integer lineNumber;
}
