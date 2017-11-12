package root.app.controllers;

import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.app.data.drawingTools.DrawLabel;
import root.app.data.runners.impl.CameraRunnerImpl;
import root.app.data.runners.impl.VideoRunnerImpl;
import root.app.data.services.DrawingService;
import root.app.data.services.ImageScaleService;
import root.app.model.LinesTableRowFX;
import root.app.model.MarkersPair;
import root.app.model.Point;
import root.app.properties.LineConfigService;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static root.app.controllers.MainController.OnClickMode.NONE;


@Slf4j
@Component
public class MainController {

    private static OnClickMode activeControl;
    private static int lineMarkersAmount = -1;
    // the FXML button
    @FXML
    private Button cameraButton;
    @FXML
    private Button videoButton;
    @FXML
    private ToggleButton markerOneButton;

    @FXML
    private ToggleButton markerTwoButton;
    // the FXML image view
    @FXML
    private ImageView imageView;
    @FXML
    private TableView<LinesTableRowFX> tableLines;

    @FXML
    private AnchorPane imageWrapperPane;

    @Autowired
    private VideoRunnerImpl videoRunner;

    @Autowired
    private CameraRunnerImpl cameraRunner;

    @Autowired
    private LineConfigService lineProvider;

    @Autowired
    private DrawingService drawingService;

    @Autowired
    private ImageScaleService scaleService;

    @FXML
    private TableColumn<LinesTableRowFX, Long> idColumn;

    @FXML
    private TableColumn<LinesTableRowFX, Integer> distanceColumn;

    @FXML
    private TableColumn<LinesTableRowFX, Button> delButton;


    private static List<MarkersPair> pairs;
    private ObservableList<LinesTableRowFX> data;

    @FXML
    void initialize() {
        activeControl = NONE;
        pairs = Lists.newArrayList();
        imageView.setOnMousePressed(mouseEventEventHandler);

        distanceColumn.setOnEditCommit((row) -> {
            lineProvider.updateDistance(row.getRowValue().getId(), row.getNewValue());
        });
    }


    private DrawLabel drawLabel = new DrawLabel();
    private final EventHandler<MouseEvent> mouseEventEventHandler = me -> {
        if (NONE.equals(activeControl)) {
            return;
        }

        double initX = me.getSceneX();
        double initY = me.getSceneY();
        double sceneHeight = imageView.getBoundsInLocal().getHeight();
        double sceneWidth = imageView.getBoundsInLocal().getWidth();
        me.consume();

        Point newPoint = new Point(initX, initY, sceneHeight, sceneWidth);
        root.app.model.Line line = drawingService.drawLines(pairs, newPoint);

        if (line != null) {
            Line fxLine = new Line(line.getStart().getX(), line.getStart().getY(), line.getEnd().getX(), line.getEnd().getY());
            fxLine.setStroke(activeControl.color);
            imageWrapperPane.getChildren().add(fxLine);
//          drawLabel.accept(pair, imageWrapperPane);

        }
    };


    /**
     * The action triggered by pushing the button on the GUI
     *
     * @param event the push button event
     */
    @FXML
    protected void startCamera(ActionEvent event) {
        log.info("Start work with camera");
        cameraRunner.setActionButton(cameraButton);
        cameraRunner.setImageView(imageView);
        cameraRunner.startCapturing();
    }

    /**
     * The action triggered by pushing the button on the GUI
     *
     * @param event the push button event
     */
    @FXML
    protected void startVideo(ActionEvent event) {
        log.info("Start work with video");
        videoRunner.setActionButton(videoButton);
        videoRunner.setImageView(imageView);
        videoRunner.startCapturing();
    }

    private void postImageViewRenderedInitialize() {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("An error occured in separate thread {} ", e.getCause(), e);
                }
                return null;
            }
        };

        sleeper.setOnSucceeded(event1 -> drawLinesAndLabels());
        new Thread(sleeper).start();
    }


    @FXML
    public void setFirstMarker(ActionEvent event) {
        if (this.markerOneButton.isSelected()) {
            MainController.activeControl = OnClickMode.FIRST_MARKER;

            markerTwoButton.setSelected(false);
        } else {
            MainController.activeControl = NONE;
        }
    }

    @FXML
    public void setSecondMarker(ActionEvent event) {
        if (this.markerTwoButton.isSelected()) {
            MainController.activeControl = OnClickMode.SECOND_MARKER;

            markerOneButton.setSelected(false);
        } else {
            MainController.activeControl = NONE;
        }
    }

    public void refresh(ActionEvent actionEvent) {
        if (lineMarkersAmount < pairs.size()) {
            drawLinesAndLabels();
        }
    }


    enum OnClickMode {
        FIRST_MARKER(Color.RED), SECOND_MARKER(Color.DARKORANGE), DRAWING(Color.DARKOLIVEGREEN),
        BIGGEST_SIZE(Color.DARKOLIVEGREEN), LEAST_SIZE(Color.CADETBLUE), NONE(null);

        Color color;

        OnClickMode(Color color) {
            this.color = color;
        }
    }

    private void drawLinesAndLabels() {
        Bounds boundsInLocal = imageView.getBoundsInLocal();
        List<MarkersPair> initLines = lineProvider.findAll();
        pairs = scaleService.fixedSize(boundsInLocal.getHeight(), boundsInLocal.getWidth(), initLines);
        lineMarkersAmount = pairs.size();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        distanceColumn.setCellFactory(TextFieldTableCell.<LinesTableRowFX, Integer>forTableColumn(new IntegerStringConverter()));
        delButton.setCellValueFactory(new PropertyValueFactory<>("delButton"));

        data = FXCollections.observableArrayList(pairs.stream().map((pair) -> {
            Button x = new Button("x");
            x.setTextFill(Color.RED);
            x.setOnAction(e -> {
                lineProvider.delete(pair);
                drawingService.removePair(imageWrapperPane, pair);
            });
            return new LinesTableRowFX(pair.getId(), pair.getDistance(), x);
        }).collect(toList()));

        tableLines.setItems(data);

        drawingService.showLines(imageWrapperPane, pairs);
    }

}

