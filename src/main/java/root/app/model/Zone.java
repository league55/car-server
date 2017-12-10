package root.app.model;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Zone extends BasicModel {
    public Zone(boolean isParent, List<Zone> childZones) {
        this.isParent = isParent;
        this.childZones = childZones;
    }

    @Builder.Default
    private Boolean isParent = false;

    private MarkersPair pair;

    @Builder.Default
    private List<Zone> childZones = Lists.newArrayList();
}
