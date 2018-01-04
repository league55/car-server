package root.app.model;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zone extends BasicModel {
    private MarkersPair pair;
    private List<Zone.ChildZone> childZones = Lists.newArrayList();

    @Data
    @AllArgsConstructor
    public static class ChildZone {
        private String id;
        private Boolean isLast;
        private MarkersPair pair;

        public ChildZone(String childZoneId, MarkersPair childZonePair) {
            this.id = childZoneId;
            this.pair = childZonePair;
            this.isLast = false;
        }
    }
}
