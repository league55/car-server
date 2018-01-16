package root.app.fx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import root.app.data.runners.impl.CameraRunnerImpl;
import root.app.data.runners.impl.VideoRunnerImpl;
import root.app.data.services.DrawingService;
import root.app.data.services.ImageScaleService;
import root.app.data.services.impl.ImageScaleServiceImpl.ScreenSize;
import root.app.fx.AnchorsService;
import root.app.model.AppConfigDTO;
import root.app.model.LinesTableRowFX;
import root.app.model.MarkersPair;
import root.app.model.Zone;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;
import root.app.properties.ConfigService;
import root.app.properties.LineConfigService;
import root.utils.Utils;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;


@Slf4j
@Component
public class MainController {

    public static String pathToVideoFile;

    private final FileChooser fileChooser = new FileChooser();

    //    -------------- FXML ----------------
    @FXML
    private Button cameraButton;
    @FXML
    private Button videoButton;
    // the FXML image view
    @FXML
    private ImageView imageView;
    @FXML
    private TableView<LinesTableRowFX> tableLines;
    @FXML
    private AnchorPane imageWrapperPane;
    @FXML
    private TextField ipInput;
    @FXML
    private TableColumn<LinesTableRowFX, Long> idColumn;
    @FXML
    private TableColumn<LinesTableRowFX, Integer> distanceColumn;
    @FXML
    private TableColumn<LinesTableRowFX, Integer> wayNum;
    @FXML
    private TableColumn<LinesTableRowFX, Button> delButton;
    @FXML
    private TextField zonesPerLineAmount;
    @FXML
    private TextField zoneHeightValue;
    @FXML
    private Button chooseFileBtn;

    //    -------------- Spring ----------------
    @Autowired
    private VideoRunnerImpl videoRunner;
    @Autowired
    private CameraRunnerImpl cameraRunner;
    @Autowired
    private LineConfigService lineProvider;
    @Autowired
    @Qualifier("zoneConfigServiceImpl")
    private ConfigService<Zone> zoneConfigService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private DrawingService drawingService;
    @Autowired
    private ImageScaleService scaleService;
    @Autowired
    private AnchorsService anchorsService;


    @FXML
    void initialize() {
        distanceColumn.setOnEditCommit((row) -> {
            final Zone zone = zoneConfigService.findOne(row.getRowValue().getId());
            zone.getPair().setDistanceLeft(row.getNewValue());
            lineProvider.updateLeftDistance(zone.getPair().getId(), row.getNewValue());
            zoneConfigService.save(zone);
        });

        wayNum.setOnEditCommit((row) -> {
            final Zone zone = zoneConfigService.findOne(row.getRowValue().getId());
            zone.getPair().setWayNum(row.getNewValue());
            lineProvider.updateWayNumber(zone.getPair().getId(), row.getNewValue());
            zoneConfigService.save(zone);
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        distanceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        wayNum.setCellValueFactory(new PropertyValueFactory<>("wayNum"));
        wayNum.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        delButton.setCellValueFactory(new PropertyValueFactory<>("delButton"));
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
        anchorsService.addNewZone(imageWrapperPane);
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
        Bounds boundsInLocal = imageView.getBoundsInLocal();
        final List<Zone> zones = zoneConfigService.findAll();

        ObservableList<LinesTableRowFX> data = FXCollections.observableArrayList(zoneConfigService.findAll().stream().map((zone) -> {
            Button x = new Button("x");
            x.setTextFill(Color.RED);
            final MarkersPair pair = zone.getPair();
            x.setOnAction(e -> {
                lineProvider.delete(pair);
                zoneConfigService.delete(zone);
                zone.getChildZones().forEach(childZone -> lineProvider.delete(childZone.getPair()));
                drawingService.removeZone(imageWrapperPane, zone);
                drawLinesAndLabels();
            });
            return new LinesTableRowFX(zone.getId(), pair.getDistanceLeft(), pair.getWayNum(), x);
        }).collect(toList()));

        tableLines.setItems(data);

        final List<MarkersPair> pairs = scaleService.fixedSize(new ScreenSize(boundsInLocal.getHeight(), boundsInLocal.getWidth()), zones.stream().map(Zone::getPair).collect(toList()));
        drawingService.showLines(imageWrapperPane, pairs);
        drawingService.showZones(imageWrapperPane, zoneConfigService.findAll());
    }

    @FXML
    private void saveZonesPerLine(ActionEvent event) {
        Integer zonesAmount = null;
        try {
            zonesAmount = Integer.parseInt(zonesPerLineAmount.getText());
        } catch (NumberFormatException e) {
            log.error("Zones per line must be integer");
        }
        if (zonesAmount != null) {
            appConfigService.save(new AppConfigDTO(ConfigAttribute.ZonesPerLineAmount, zonesAmount + ""));
            log.info("Zones per line now: {}", zonesAmount);
        }
    }

    ;

    @FXML
    private void saveFirstZoneHeight(ActionEvent event) {
        Integer zonesHeight = null;
        try {
            zonesHeight = Integer.parseInt(zoneHeightValue.getText());
        } catch (NumberFormatException e) {
            log.error("Zones per line must be integer");
        }
        if (zonesHeight != null) {
            appConfigService.save(new AppConfigDTO(ConfigAttribute.ZoneHeight, zonesHeight + ""));
            log.info("Zones height now: {}", zonesHeight);
        }
    }

    ;

    @FXML
    private void saveIpAction(ActionEvent event) {
        String IP = ipInput.getText();
        if (IP != null) {
            appConfigService.save(new AppConfigDTO(ConfigAttribute.CameraIP, IP));
        }
    }

}

