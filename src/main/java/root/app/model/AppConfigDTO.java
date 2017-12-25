package root.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import root.app.properties.ConfigAttribute;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppConfigDTO<T> extends BasicModel {

    private ConfigAttribute key;
    private T value;
}

