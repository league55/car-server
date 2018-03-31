package root.app.controllers.request;

import lombok.Data;

@Data
public class GridUpdateRequest {
    private String calibrationType;
    private Double value;
    private Integer lineNumber;
}
