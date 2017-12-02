package root.app.controllers;

import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.app.data.runners.impl.CameraRunnerImpl;
import root.app.data.runners.impl.VideoRunnerImpl;
import root.app.data.services.DrawingService;
import root.app.data.services.ImageScaleService;
import root.app.model.LinesTableRowFX;
import root.app.model.MarkersPair;
import root.app.model.Point;
import root.app.properties.LineConfigService;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static root.app.controllers.MainController.OnClickMode.NONE;


@Slf4j
@Component
public class MainController {

    public static String pathToVideoFile;

    private static OnClickMode activeControl;
    private static int lineMarkersAmount = -1;
    private static List<MarkersPair> pairs;
    private final FileChooser fileChooser = new FileChooser();

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
    private TableColumn<LinesTableRowFX, Integer> wayNum;

    @FXML
    private TableColumn<LinesTableRowFX, Button> delButton;

    @FXML
    private Button chooseFileBtn;


    @FXML
    void initialize() {
        activeControl = NONE;
        pairs = Lists.newArrayList();
        imageView.setOnMousePressed(mouseEventEventHandler);

        distanceColumn.setOnEditCommit((row) -> lineProvider.updateLeftDistance(row.getRowValue().getId(), row.getNewValue()));
        wayNum.setOnEditCommit((row) -> lineProvider.updateWayNumber(row.getRowValue().getId(), row.getNewValue()));
    }


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
        if (pathToVideoFile == null) {
            chooseFile(event);
            if (pathToVideoFile == null) return;
        }

        videoRunner.setActionButton(videoButton);
        videoRunner.setImageView(imageView);
        videoRunner.startCapturing();
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

    @FXML
    public void chooseFile(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(chooseFileBtn.getScene().getWindow());
        if (file != null) {
            pathToVideoFile = file.getAbsolutePath();
        }
    }


    enum OnClickMode {
        FIRST_MARKER(Color.RED), SECOND_MARKER(Color.DARKORANGE), NONE(null);

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
        distanceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        wayNum.setCellValueFactory(new PropertyValueFactory<>("wayNum"));
        wayNum.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        delButton.setCellValueFactory(new PropertyValueFactory<>("delButton"));

        ObservableList<LinesTableRowFX> data = FXCollections.observableArrayList(pairs.stream().map((pair) -> {
            Button x = new Button("x");
            x.setTextFill(Color.RED);
            x.setOnAction(e -> {
                lineProvider.delete(pair);
                drawingService.removePair(imageWrapperPane, pair);
                drawLinesAndLabels();
            });
            return new LinesTableRowFX(pair.getId(), pair.getDistanceLeft(), pair.getWayNum(), x);
        }).collect(toList()));

        tableLines.setItems(data);

        drawingService.showLines(imageWrapperPane, pairs);
    }

}

