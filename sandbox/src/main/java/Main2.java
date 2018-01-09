package main.java;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Effect;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

public class Main2 extends Application {

    private final int OFFSET = 50;

    public static void main(String[] args) {
        Application.launch(args);
    }

    final Slider xPivotSlider = createSlider(50, "Best values are 0, 50 or 100");
    final Slider yPivotSlider = createSlider(50, "Best values are 0, 50 or 100");
    final Slider zPivotSlider = createSlider(0, "Won't do anything until you use an X or Y axis of rotation");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("");
        Group root = new Group();
        Scene scene = new Scene(root, 500, 500, Color.WHITE);

        Group g = getRectGrpoup(3, 3);

        final ObservableList<Double> points = FXCollections.observableArrayList();

        points.addAll(10.0, 10.0, 310.0, 40.0, 310.0, 150.0, 10.0, 90.0);

        g.setCache(true);
        g.setLayoutX(OFFSET);
        g.setLayoutY(OFFSET);

        root.getChildren().add(g);

        xPivotSlider.setLayoutY(20);
        yPivotSlider.setLayoutY(40);
        zPivotSlider.setLayoutY(60);
        root.getChildren().addAll(xPivotSlider, yPivotSlider, zPivotSlider);
        root.getChildren().addAll(createControlAnchorsFor(points));

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private Effect getPerspectiveTransform(ObservableList<Double> points) {
        PerspectiveTransform pt = new PerspectiveTransform();
        pt.setUlx(points.get(0));
        pt.setUly(points.get(1));
        pt.setUrx(points.get(2));
        pt.setUry(points.get(3));
        pt.setLrx(points.get(4));
        pt.setLry(points.get(5));
        pt.setLlx(points.get(6));
        pt.setLly(points.get(7));

        return pt;
    }

    Group getRectGrpoup(int lines, int zones) {
        Group g = new Group();
        for (int k = 0; k < zones; k++) {
            for (int i = 0; i < lines; i++) {
                Rectangle r = new Rectangle();
                r.setOnMousePressed(event -> {
                    System.out.println(r.contains(event.getX(), event.getY()));
                });
                r.setY(i * 17);
                r.setX(k * 17);
                r.setWidth(15);
                r.setHeight(15);
                r.setFill(Color.RED);
                g.getChildren().add(r);
            }
        }
        return g;
    }

    Slider getSlider() {
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(360);
        slider.setValue(40);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(50);
        slider.setMinorTickCount(5);
        slider.setBlockIncrement(10);

        return slider;
    }

    private Slider createSlider(final double value, final String helpText) {
        final Slider slider = new Slider(-50, 151, value);
        slider.setMajorTickUnit(50);
        slider.setMinorTickCount(0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setStyle("-fx-text-fill: white");
        slider.setTooltip(new Tooltip(helpText));
        return slider;
    }

    private ObservableList<Anchor> createControlAnchorsFor(final ObservableList<Double> points) {
        ObservableList<Anchor> anchors = FXCollections.observableArrayList();

        for (int i = 0; i < points.size(); i += 2) {
            final int idx = i;

            DoubleProperty xProperty = new SimpleDoubleProperty(points.get(i));
            DoubleProperty yProperty = new SimpleDoubleProperty(points.get(i + 1));

            xProperty.addListener((ov, oldX, x) -> points.set(idx, (double) x));

            yProperty.addListener((ov, oldY, y) -> points.set(idx + 1, (double) y));

            anchors.add(new Anchor(Color.GOLD, xProperty, yProperty));
        }

        return anchors;
    }

    class Anchor extends Circle {
        private final DoubleProperty x, y;

        Anchor(Color color, DoubleProperty x, DoubleProperty y) {
            super(x.get() + OFFSET, y.get() + OFFSET, 5);
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