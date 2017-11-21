package root.app.model;

import javafx.scene.control.Button;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LinesTableRowFX {
    Long id;
    Integer distance;
    Integer wayNum;
    Button delButton;
}
