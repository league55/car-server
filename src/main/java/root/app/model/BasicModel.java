package root.app.model;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class BasicModel implements Serializable {
    public static String getName() {
        return null;
    }

    private Long id;
}
