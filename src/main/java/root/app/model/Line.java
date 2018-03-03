package root.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Line implements Cloneable, Serializable{

    private Point start;

    private Point end;

    @Override
    public Line clone()  {
        return new Line(start.clone(), end.clone());
    }
}
