package root.app.data.drawingTools;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import root.app.model.MarkersPair;
import root.app.model.RoadWay;

import java.util.function.BiConsumer;

import static root.app.data.services.ZoneComputingService.ZONE_PREFIX;

/**
 * Created by maksym on 30.10.17.
 */
public class DrawLabel implements BiConsumer<RoadWay, AnchorPane> {
    @Override
    public void accept(RoadWay way, AnchorPane anchorPane) {
        MarkersPair  markersPair = way.getPair();

        Label label = new Label(markersPair.getId().toString());
        label.setFont(Font.font(24));
        label.setTextFill(Color.LIME);
        label.setId(ZONE_PREFIX + way.getId());

        final double xCenter = markersPair.getLineA().getEnd().getX() - (markersPair.getLineA().getEnd().getX() - markersPair.getLineA().getStart().getX()) / 2;
        AnchorPane.setLeftAnchor(label, xCenter);
        AnchorPane.setTopAnchor(label, markersPair.getLineA().getEnd().getY() + 60);
        anchorPane.getChildren().add(label);
    }
}
