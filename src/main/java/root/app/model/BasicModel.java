package root.app.model;

import lombok.Data;

@Data
public abstract class BasicModel {
    public static String getName() {
        return null;
    }

    private Long id;
}
