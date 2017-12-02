package root.app.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class Zone extends BasicModel {

    public Zone(MarkersPair pair, boolean isParent, List<Zone> childZones) {
        this.pair = pair;
        this.isParent = isParent;
        this.childZones = childZones;
    }

    MarkersPair pair;

    @Builder.Default
    boolean isParent = false;
    List<Zone> childZones;
}
