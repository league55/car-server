package root.app.model;

import lombok.Data;

@Data
public class CrossedPair {

    private String zoneId;

    private Long timeEnteredZone;
    private Long timeLeavedZone;

    private Double speed;
}
