package root.app.controllers;

import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
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
import root.app.model.*;
import root.app.properties.ConfigAttribute;
import root.app.properties.ConfigService;
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
    private ConfigService<Zone> zoneConfigService;

    @Autowired
    private ConfigService<AppConfigDTO> appConfigService;

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
    private Button saveZonesPerLineAmount;
    @FXML
    private Button zoneHeightButton;
    @FXML
    private TextField zonesPerLineAmount;
    @FXML
    private TextField zoneHeightValue;
    @FXML
    private Button chooseFileBtn;


    @FXML
    void initialize() {
        activeControl = NONE;
        pairs = Lists.newArrayList();
        imageView.setOnMousePressed(mouseEventEventHandler);

        distanceColumn.setOnEditCommit((row) -> lineProvider.updateLeftDistance(row.getRowValue().getId(), row.getNewValue()));
        wayNum.setOnEditCommit((row) -> lineProvider.updateWayNumber(row.getRowValue().getId(), row.getNewValue()));
        saveZonesPerLineAmount.setOnAction(event -> {
            Integer zonesAmount = null;
            try {
                zonesAmount = Integer.parseInt(zonesPerLineAmount.getText());
            } catch (NumberFormatException e) {
                log.error("Zones per line must be integer");
            }
            if (zonesAmount != null) {
                final AppConfigDTO prevZonesPerLine = appConfigService
                        .findAll()
                        .stream()
                        .filter(c -> ConfigAttribute.ZonesPerLineAmount.equals(c.getKey()))
                        .findFirst()
                        .orElse(new AppConfigDTO());
                prevZonesPerLine.setValue(zonesAmount);
                appConfigService.save(prevZonesPerLine);
                log.info("Zones per line now: {}", zonesAmount);
            }
        });

        zoneHeightButton.setOnAction(event -> {
            Integer zonesHeight = null;
            try {
                zonesHeight = Integer.parseInt(zoneHeightValue.getText());
            } catch (NumberFormatException e) {
                log.error("Zones per line must be integer");
            }
            if (zonesHeight != null) {
                final AppConfigDTO prevZoneHeight = appConfigService
                        .findAll()
                        .stream()
                        .filter(c -> ConfigAttribute.ZoneHeight.equals(c.getKey()))
                        .findFirst()
                        .orElse(new AppConfigDTO());
                prevZoneHeight.setValue(zonesHeight);
                appConfigService.save(prevZoneHeight);
                log.info("Zones height now: {}", zonesHeight);
            }
        });
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
        lineMarkersAmount = initLines.size();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        distanceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        wayNum.setCellValueFactory(new PropertyValueFactory<>("wayNum"));
        wayNum.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        delButton.setCellValueFactory(new PropertyValueFactory<>("delButton"));

        ObservableList<LinesTableRowFX> data = FXCollections.observableArrayList(zoneConfigService.findAll().stream().map((zone) -> {
            Button x = new Button("x");
            x.setTextFill(Color.RED);
            final MarkersPair pair = zone.getPair();
            x.setOnAction(e -> {
                lineProvider.delete(pair);
                zoneConfigService.delete(zone);
                drawingService.removeZone(imageWrapperPane, zone);
                drawLinesAndLabels();
            });
            return new LinesTableRowFX(zone.getId(), pair.getDistanceLeft(), pair.getWayNum(), x);
        }).collect(toList()));

        tableLines.setItems(data);

        drawingService.showLines(imageWrapperPane, pairs);
        drawingService.showZones(imageWrapperPane, zoneConfigService.findAll());
    }

    public ImageView getImageView() {
        return imageView;
    }

    public AnchorPane getImageWrapperPane() {
        return imageWrapperPane;
    }



}

