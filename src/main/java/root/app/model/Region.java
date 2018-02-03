package root.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region extends BasicModel  {
    private MarkersPair regionBounds;

    private List<Point> offsets;
}
