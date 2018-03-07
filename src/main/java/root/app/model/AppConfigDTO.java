package root.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import root.app.properties.ConfigAttribute;

import static ch.qos.logback.core.util.OptionHelper.isEmpty;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppConfigDTO extends BasicModel {

    private ConfigAttribute key;
    private String value;

    public String getValue() {
        return isEmpty(value) ? key.getDefaultValue() : value;
    }
}

