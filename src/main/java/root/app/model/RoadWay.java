package root.app.model;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadWay extends BasicModel implements Cloneable {
    private MarkersPair pair;
    private List<Zone> zones = Lists.newArrayList();

    @Data
    @AllArgsConstructor
    public static class Zone implements Serializable, Cloneable{
        private String id;
        private Boolean isLast;
        private MarkersPair pair;

        public Zone(String childZoneId, MarkersPair childZonePair) {
            this.id = childZoneId;
            this.pair = childZonePair;
            this.isLast = false;
        }

        @Override
        public Zone clone() {
            return new Zone(id, isLast, pair.clone());
        }
    }


    @Override
    public RoadWay clone() {
        return new RoadWay(pair.clone(), zones.stream().map(Zone::clone).collect(toList()));
    }
}
