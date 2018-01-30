package root.app.fx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import root.app.data.runners.impl.CameraRunnerImpl;
import root.app.data.runners.impl.VideoRunnerImpl;
import root.app.data.services.CalibrationService;
import root.app.data.services.DrawingService;
import root.app.data.services.ImageScaleService;
import root.app.data.services.impl.ImageScaleServiceImpl.ScreenSize;
import root.app.fx.AnchorsService;
import root.app.model.*;
import root.app.properties.*;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Slf4j
@Component
public class MainController {
    private final FileChooser fileChooser = new FileChooser();
    private final ToggleGroup zoneNumberRadioGroup = new ToggleGroup();

    //    -------------- FXML ----------------
    public Button cameraButton;
    public Button videoButton;
    public ImageView imageView;
    public TableView<LinesTableRowFX> tableLines;
    public AnchorPane imageWrapperPane;
    public TextField ipInput;
    public TableColumn<LinesTableRowFX, Long> idColumn;
    public TableColumn<LinesTableRowFX, Integer> distanceColumn;
    public TableColumn<LinesTableRowFX, Integer> wayNum;
    public TableColumn<LinesTableRowFX, Button> delButton;
    public TextField zonesPerLineAmountInput;
    public TextField zoneHeightInput;
    public Button chooseFileBtn;
    public TextField deltaTimeInput;
    public Label deltaTimeLabel;
    public Label ipLabel;
    public Label zoneAmountLabel;
    public FlowPane zoneNumbersGroup;
    public Slider horizMoveSlider;
    public Slider verticalMoveSlider;

    //    -------------- Spring ----------------
    @Autowired
    private VideoRunnerImpl videoRunner;
    @Autowired
    private CameraRunnerImpl cameraRunner;
    @Autowired
    private LineConfigService lineProvider;
    @Autowired
    @Qualifier("zoneConfigServiceImpl")
    private ConfigService<RoadWay> zoneConfigService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private PolygonConfigService polygonConfigService;
    @Autowired
    private DrawingService drawingService;
    @Autowired
    private ImageScaleService scaleService;
    @Autowired
    private AnchorsService anchorsService;
    @Autowired
    private CalibrationService calibrationService;


    @FXML
    void initialize() {
        distanceColumn.setOnEditCommit((row) -> {
            final RoadWay way = zoneConfigService.findOne(row.getRowValue().getId());
            way.getPair().setDistanceLeft(row.getNewValue());
            lineProvider.updateLeftDistance(way.getPair().getId(), row.getNewValue());
            zoneConfigService.save(way);
        });

        wayNum.setOnEditCommit((row) -> {
            final RoadWay way = zoneConfigService.findOne(row.getRowValue().getId());
            way.getPair().setWayNum(row.getNewValue());
            lineProvider.updateWayNumber(way.getPair().getId(), row.getNewValue());
            zoneConfigService.save(way);
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        distanceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        wayNum.setCellValueFactory(new PropertyValueFactory<>("wayNum"));
        wayNum.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        delButton.setCellValueFactory(new PropertyValueFactory<>("delButton"));
        initializeInputs();
        initializeCalibratingTab();
    }

    private void initializeCalibratingTab() {
        initSubZoneRadioGroup();
        initCalibratingSliders();
    }

    private void initCalibratingSliders() {
        verticalMoveSlider.setMin(-300);
        verticalMoveSlider.setMax(300);
        verticalMoveSlider.setMajorTickUnit(50);
        verticalMoveSlider.setMinorTickCount(5);
        verticalMoveSlider.setValue(0);
        verticalMoveSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            final Integer userData = (Integer) zoneNumberRadioGroup.getSelectedToggle().getUserData();
            calibrationService.fixPosition(userData, oldValue.doubleValue() - newValue.doubleValue());
            drawingService.clearAll(imageWrapperPane);
            redrawLinesAndZones();
        });
    }

    private void initSubZoneRadioGroup() {
        final String amount = appConfigService.findOne(ConfigAttribute.ZonesPerLineAmount).getValue();
        final Integer zonesAmount = Integer.valueOf(amount);
        final Insets insets = new Insets(10);
        for (Integer i = 0; i < zonesAmount; i++) {
            final RadioButton button = new RadioButton(i.toString());
            button.setToggleGroup(zoneNumberRadioGroup);
            button.setUserData(i);
            zoneNumbersGroup.getChildren().add(button);
            button.setPadding(insets);
            button.setOnAction(event -> verticalMoveSlider.setValue(0));
        }

    }

    private void initializeInputs() {
        deltaTimeInput.setText(appConfigService.findOne(ConfigAttribute.TimeBetweenOutput).getValue());
        ipInput.setText(appConfigService.findOne(ConfigAttribute.CameraIP).getValue());
        zonesPerLineAmountInput.setText(appConfigService.findOne(ConfigAttribute.ZonesPerLineAmount).getValue());
        zoneHeightInput.setText(appConfigService.findOne(ConfigAttribute.ZoneHeight).getValue());


        deltaTimeLabel.setTooltip(new Tooltip("Время подсчета авто"));
        ipLabel.setTooltip(new Tooltip("IP адресс камеры"));
        zoneAmountLabel.setTooltip(new Tooltip("Кол-во зон на полосе"));
    }

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
        cameraRunner.setContainerPane(imageWrapperPane);
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
        if (appConfigService.findOne(ConfigAttribute.PathToVideoFile).getValue() == null) {
            final Long pathToVideoFile = chooseVideoFile();
            if (pathToVideoFile == null) return;
        }

        videoRunner.setActionButton(videoButton);
        videoRunner.setImageView(imageView);
        videoRunner.setContainerPane(imageWrapperPane);
        videoRunner.startCapturing();

    }

    @FXML
    public void chooseFile(ActionEvent actionEvent) {
        chooseVideoFile();
    }

    @FXML
    private void refresh(ActionEvent actionEvent) {
        drawLinesAndLabels();
    }

    @FXML
    private void addAnchors(ActionEvent actionEvent) {
        anchorsService.addAnchorsGroup(imageWrapperPane);
    }

    @FXML
    private void submitZone(ActionEvent actionEvent) {
        double sceneHeight = imageView.getBoundsInLocal().getHeight();
        double sceneWidth = imageView.getBoundsInLocal().getWidth();

        drawingService.submitZone(anchorsService.getCoordinates(sceneHeight, sceneWidth), imageWrapperPane);
        anchorsService.clean(imageWrapperPane);

        drawLinesAndLabels();
    }

    @FXML
    private void cleanAnchors(ActionEvent actionEvent) {
        anchorsService.clean(imageWrapperPane);
    }

    private Long chooseVideoFile() {
        File file = fileChooser.showOpenDialog(chooseFileBtn.getScene().getWindow());
        if (file != null) {
            return appConfigService.save(new AppConfigDTO(ConfigAttribute.PathToVideoFile, file.getAbsolutePath()));
        }
        return null;
    }

    private void drawLinesAndLabels() {
        ObservableList<LinesTableRowFX> data = FXCollections.observableArrayList(zoneConfigService.findAll().stream().map((zone) -> {
            Button x = new Button("x");
            x.setTextFill(Color.RED);
            final MarkersPair pair = zone.getPair();
            x.setOnAction(e -> {
                lineProvider.delete(pair);
                zoneConfigService.delete(zone);
                zone.getZones().forEach(childZone -> lineProvider.delete(childZone.getPair()));
                drawingService.removeZone(imageWrapperPane, zone);
                drawLinesAndLabels();
            });
            return new LinesTableRowFX(zone.getId(), pair.getDistanceLeft(), pair.getWayNum(), x);
        }).collect(toList()));

        tableLines.setItems(data);

        redrawLinesAndZones();
    }

    private void redrawLinesAndZones() {
        final List<RoadWay> waysList = zoneConfigService.findAll();
        Bounds boundsInLocal = imageView.getBoundsInLocal();
        final ScreenSize screenSize = new ScreenSize(boundsInLocal.getHeight(), boundsInLocal.getWidth());
        final List<MarkersPair> pairs = scaleService.fixedSize(screenSize, waysList.stream().map(RoadWay::getPair).collect(toList()));
        drawingService.showLines(imageWrapperPane, pairs);
        drawingService.showZones(imageWrapperPane, zoneConfigService.findAll(), screenSize);
    }

    @FXML
    private void saveZonesPerLine(ActionEvent event) {
        Integer zonesAmount = null;
        try {
            zonesAmount = Integer.parseInt(zonesPerLineAmountInput.getText());
        } catch (NumberFormatException e) {
            log.error("Zones per line must be integer");
        }
        if (zonesAmount != null) {
            appConfigService.save(new AppConfigDTO(ConfigAttribute.ZonesPerLineAmount, zonesAmount + ""));
            log.info("Zones per line now: {}", zonesAmount);
            initSubZoneRadioGroup();
        }
    }

    @FXML
    private void saveFirstZoneHeight(ActionEvent event) {
        Integer zonesHeight = null;
        try {
            zonesHeight = Integer.parseInt(zoneHeightInput.getText());
        } catch (NumberFormatException e) {
            log.error("Zones per line must be integer");
        }
        if (zonesHeight != null) {
            appConfigService.save(new AppConfigDTO(ConfigAttribute.ZoneHeight, zonesHeight + ""));
            log.info("Zones height now: {}", zonesHeight);
        }
    }

    @FXML
    private void saveIpAction(ActionEvent event) {
        String IP = ipInput.getText();
        if (IP != null) {
            appConfigService.save(new AppConfigDTO(ConfigAttribute.CameraIP, IP));
        }
    }

    @FXML
    private void saveDeltaTime(ActionEvent actionEvent) {
        try {
            Integer timeBetweenOutput = Integer.parseInt(deltaTimeInput.getText());
            appConfigService.save(new AppConfigDTO(ConfigAttribute.TimeBetweenOutput, timeBetweenOutput.toString()));
        } catch (NumberFormatException e) {
            log.info("Not integer in time between frames input ");
        }
    }

    @FXML
    private void submitPesrsp(ActionEvent actionEvent) {
        final Bounds boundsInLocal = imageView.getBoundsInLocal();
        final MarkersPair coordinates = anchorsService.getCoordinates(boundsInLocal.getHeight(), boundsInLocal.getWidth());
        polygonConfigService.save(
                new PolygonDTO(coordinates.getLineB().getStart(), //top left
                        coordinates.getLineB().getEnd(),        //top right
                        coordinates.getLineA().getEnd(),        //bot right
                        coordinates.getLineA().getStart(),      //bot left
                        PolygonDTO.Destination.ROI));
    }
}

