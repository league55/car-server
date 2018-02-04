package root.app.data.drawingTools;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import root.app.model.MarkersPair;
import root.app.model.RoadWay;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static root.app.data.services.ZoneComputingService.ZONE_PREFIX;

/**
 * Created by maksym on 30.10.17.
 */
public class ZoneLabel implements Function<RoadWay.Zone, Label> {
    @Override
    public Label apply(RoadWay.Zone zone) {

        Label label = new Label(zone.getId().substring(ZONE_PREFIX.length()));
        label.setFont(Font.font(16));
        label.setTextFill(Color.LIME);
        label.setId(ZONE_PREFIX + zone.getId());

        final MarkersPair markersPair = zone.getPair();
        final double xCenter = markersPair.getLineA().getEnd().getX() - (markersPair.getLineA().getEnd().getX() - markersPair.getLineA().getStart().getX()) / 2;
        AnchorPane.setLeftAnchor(label, xCenter);
        AnchorPane.setTopAnchor(label, markersPair.getLineA().getEnd().getY() - 30);
        return label;
    }
}
