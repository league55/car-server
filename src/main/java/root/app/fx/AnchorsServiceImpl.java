package root.app.fx;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Service;
import root.app.model.Line;
import root.app.model.MarkersPair;
import root.app.model.Point;

@Service
public class AnchorsServiceImpl implements AnchorsService {

    private final String ANCHOR = "ANCHOR_";

    private ObservableList<Double> points;

    @Override
    public void addAnchorsGroup(Pane imagePane) {
        clean(imagePane);

        points.addAll(10.0, 50.0, 50.0, 50.0, 10.0, 10.0, 50.0, 10.0);
        imagePane.getChildren().addAll(createControlAnchorsFor(points));
    }

    @Override
    public void clean(Pane imagePane) {
        points = FXCollections.observableArrayList();
        imagePane.getChildren().removeIf(child -> child.getId() != null && child.getId().contains(ANCHOR));
    }

    @Override
    public MarkersPair getCoordinates(double sceneHeight, double sceneWidth) {
        MarkersPair pair = new MarkersPair();
        pair.setLineA(new Line(new Point(points.get(0), points.get(1), sceneHeight, sceneWidth), new Point(points.get(2), points.get(3), sceneHeight, sceneWidth)));
        pair.setLineB(new Line(new Point(points.get(4), points.get(5), sceneHeight, sceneWidth), new Point(points.get(6), points.get(7), sceneHeight, sceneWidth)));

        return pair;
    }

    private ObservableList<Anchor> createControlAnchorsFor(final ObservableList<Double> points) {
        ObservableList<Anchor> anchors = FXCollections.observableArrayList();

        for (int i = 0; i < points.size(); i += 2) {
            final int idx = i;

            DoubleProperty xProperty = new SimpleDoubleProperty(points.get(i));
            DoubleProperty yProperty = new SimpleDoubleProperty(points.get(i + 1));

            xProperty.addListener((ov, oldX, x) -> points.set(idx, (double) x));

            yProperty.addListener((ov, oldY, y) -> points.set(idx + 1, (double) y));

            anchors.add(new Anchor(i, Color.GOLD, xProperty, yProperty));
        }

        return anchors;
    }
}