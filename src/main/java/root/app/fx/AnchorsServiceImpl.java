package root.app.fx;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import org.springframework.stereotype.Service;
import root.app.model.Line;
import root.app.model.MarkersPair;
import root.app.model.Point;

@Service
public class AnchorsServiceImpl implements AnchorsService {

    private final int OFFSET = 50;
    private final String ANCHOR = "ANCHOR_";

    private ObservableList<Double> points;

    @Override
    public void addNewZone(Pane imagePane) {
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

    class Anchor extends Circle {
        private final DoubleProperty x, y;

        Anchor(int id, Color color, DoubleProperty x, DoubleProperty y) {
            super(x.get() + OFFSET, y.get() + OFFSET, 5);
            this.setId(ANCHOR + id);

            setFill(color.deriveColor(1, 1, 1, 0.5));
            setStroke(color);
            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);

            this.x = x;
            this.y = y;

            x.bind(centerXProperty());
            y.bind(centerYProperty());
            enableDrag();
        }

        // make a node movable by dragging it around with the mouse.
        private void enableDrag() {
            final Delta dragDelta = new Delta();
            setOnMousePressed(mouseEvent -> {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getCenterX() - mouseEvent.getX();
                dragDelta.y = getCenterY() - mouseEvent.getY();
                getScene().setCursor(Cursor.MOVE);
            });
            setOnMouseReleased(mouseEvent -> getScene().setCursor(Cursor.HAND));
            setOnMouseDragged(mouseEvent -> {
                double newX = mouseEvent.getX() + dragDelta.x;
                if (newX > 0 && newX < getScene().getWidth()) {
                    setCenterX(newX);
                }
                double newY = mouseEvent.getY() + dragDelta.y;
                if (newY > 0 && newY < getScene().getHeight()) {
                    setCenterY(newY);
                }
            });
            setOnMouseEntered(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.HAND);
                }
            });
            setOnMouseExited(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.DEFAULT);
                }
            });
        }

        // records relative x and y co-ordinates.
        private class Delta {
            double x, y;
        }
    }
}