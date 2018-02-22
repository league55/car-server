package root.app.properties;

import lombok.Data;


public enum ConfigAttribute {
    ZonesPerLineAmount("1"),
    CameraIP("http://192.168.1.1"),
    PathToVideoFile("C:\\CarsDrivingUnderBridge.mp4"),
    ZoneHeight("5"),
    RoadWaysAmount("1"),
    TimeBetweenOutput("5000"),
    MinimumCarArea("7000");

    private String defaultValue;

    ConfigAttribute(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public String getDefaultValue() {
        return defaultValue;
    }
}
