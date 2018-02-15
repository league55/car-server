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
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import root.app.data.runners.impl.CameraRunnerImpl;
import root.app.data.runners.impl.VideoRunnerImpl;
import root.app.data.services.*;
import root.app.data.services.impl.ImageScaleServiceImpl.ScreenSize;
import root.app.fx.AnchorsService;
import root.app.model.*;
import root.app.properties.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;
import static root.app.data.services.ZoneComputingService.ZONE_PREFIX;


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
    public Slider verticalMoveSlider;
    public TextField deleteRowInput;

    //    -------------- Spring ----------------
    @Autowired
    private VideoRunnerImpl videoRunner;
    @Autowired
    private CameraRunnerImpl cameraRunner;
    @Autowired
    @Qualifier("roadWaysConfigServiceImpl")
    private RoadWaysConfigService zoneConfigService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private ReconfiguringService reconfiguringService;
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
    @Autowired
    private ApplicationStateService applicationStateService;


    @FXML
    void initialize() {
        distanceColumn.setOnEditCommit((row) -> {
            final List<RoadWay.Zone> roadRow = zoneConfigService.findRow(row.getRowValue().getId());
            roadRow.forEach(r -> r.getPair().setRealDistance(row.getNewValue()));
            zoneConfigService.saveZones(roadRow);
            drawLinesAndLabelsAndTable();
        });

        wayNum.setOnEditCommit((row) -> {
            final List<RoadWay.Zone> roadRow = zoneConfigService.findRow(row.getRowValue().getId());
            roadRow.forEach(r -> r.getPair().setWayNum(row.getNewValue()));
            zoneConfigService.saveZones(roadRow);
            drawLinesAndLabelsAndTable();
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        distanceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        wayNum.setCellValueFactory(new PropertyValueFactory<>("wayNum"));
        wayNum.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
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
        verticalMoveSlider.adjustValue(0);
    }

    private void initSubZoneRadioGroup() {
        zoneNumbersGroup.getChildren().clear();

        final String amount = appConfigService.findOne(ConfigAttribute.ZonesPerLineAmount).getValue();
        final Integer zonesAmount = Integer.valueOf(amount);
        final Insets insets = new Insets(10);
        for (Integer i = 0; i < zonesAmount - 1; i++) {
            final RadioButton button = new RadioButton(i + 1 + "");
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
        drawLinesAndLabelsAndTable();
    }

    @FXML
    private void addAnchors(ActionEvent actionEvent) {
        anchorsService.addAnchorsGroup(imageWrapperPane);
    }

    @FXML
    private void submitZone(ActionEvent actionEvent) {
        double sceneHeight = imageView.getBoundsInLocal().getHeight();
        double sceneWidth = imageView.getBoundsInLocal().getWidth();

        drawingService.submitRegion(anchorsService.getCoordinates(sceneHeight, sceneWidth), imageWrapperPane);
        anchorsService.clean(imageWrapperPane);

        drawLinesAndLabelsAndTable();
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

    private void drawLinesAndLabelsAndTable() {
        ObservableList<LinesTableRowFX> data = FXCollections.observableArrayList(zoneConfigService.findAllZones().stream().map((zone) -> {
            final MarkersPair pair = zone.getPair();
            return new LinesTableRowFX(zone.getId().substring(ZONE_PREFIX.length()), pair.getRealDistance(), pair.getWayNum());
        }).collect(toList()));

        tableLines.setItems(data);
        initSubZoneRadioGroup();
        redrawLinesAndZones();
    }

    private void redrawLinesAndZones() {
        final List<RoadWay> waysList = zoneConfigService.findAll();
        Bounds boundsInLocal = imageView.getBoundsInLocal();
        final ScreenSize screenSize = new ScreenSize(boundsInLocal.getHeight(), boundsInLocal.getWidth());
        final List<MarkersPair> pairs = scaleService.fixedSize(screenSize, waysList.stream().map(RoadWay::getPair).collect(toList()));
        drawingService.showLines(imageWrapperPane, pairs);
        drawingService.showZones(imageWrapperPane, screenSize);
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
    private void submitROI(ActionEvent actionEvent) {
        final Bounds boundsInLocal = imageView.getBoundsInLocal();
        final MarkersPair coordinates = anchorsService.getCoordinates(boundsInLocal.getHeight(), boundsInLocal.getWidth());
        polygonConfigService.save(
                new PolygonDTO(coordinates.getLineB().getStart(), //top left
                        coordinates.getLineB().getEnd(),        //top right
                        coordinates.getLineA().getEnd(),        //bot right
                        coordinates.getLineA().getStart(),      //bot left
                        PolygonDTO.Destination.ROI));
    }

    public void resetZones(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        final ImageView graphic = new ImageView(this.getClass().getResource("/static/icons/achtung.gif").toString());
        graphic.setFitWidth(48);
        graphic.setFitHeight(48);
        alert.setGraphic(graphic);

        alert.setTitle("Удалить зоны");
        alert.setHeaderText("Вы собираетесь удалить все зоны");
        alert.setContentText("Это действие необратимо и потребуется перекалибровка приложения");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            applicationStateService.resetZones();
            drawingService.clearAll(imageWrapperPane);
            drawLinesAndLabelsAndTable();
        }


    }

    public void deleteRow(ActionEvent actionEvent) {
        if(isEmpty(deleteRowInput.getText())) return;
        try {
            reconfiguringService.removeRow(Integer.parseInt(deleteRowInput.getText()));
            drawingService.clearAll(imageWrapperPane);
            drawLinesAndLabelsAndTable();
        } catch (NumberFormatException e) {
            log.error("Trying to delete non integer row ", e);
        }
    }
}

