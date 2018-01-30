package root.app.model;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadWay extends BasicModel {
    private MarkersPair pair;
    private List<Zone> zones = Lists.newArrayList();

    @Data
    @AllArgsConstructor
    public static class Zone {
        private String id;
        private Boolean isLast;
        private MarkersPair pair;

        public Zone(String childZoneId, MarkersPair childZonePair) {
            this.id = childZoneId;
            this.pair = childZonePair;
            this.isLast = false;
        }
    }
}
