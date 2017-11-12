package root.app.data.drawingTools;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import root.app.model.MarkersPair;

import java.util.function.BiConsumer;

import static root.app.data.services.impl.DrawingServiceImpl.LABEL_PREFIX;

/**
 * Created by maksym on 30.10.17.
 */
public class DrawLabel implements BiConsumer<MarkersPair, AnchorPane> {
    @Override
    public void accept(MarkersPair markersPair, AnchorPane anchorPane) {
        Label label = new Label(markersPair.getId().toString());
        label.setId(LABEL_PREFIX + markersPair.getId());

        AnchorPane.setLeftAnchor(label, markersPair.getLineB().getEnd().getX());
        AnchorPane.setTopAnchor(label, markersPair.getLineB().getEnd().getY());
        anchorPane.getChildren().add(label);
    }
}
