package root.app.data.runners.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import root.app.data.detectors.Detector;
import root.app.data.processors.DetectedCarProcessor;
import root.app.data.runners.BasicRunner;
import root.app.data.services.*;
import root.app.model.Zone;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;
import root.app.properties.ConfigService;
import root.app.properties.PolygonConfigService;;

/**
 * Use video from camera
 */
@Component
@Slf4j
public class CameraRunnerImpl extends BasicRunner {

    @Autowired
    public CameraRunnerImpl(
            DataOutputService dataOutputService,
            AppConfigService appConfigService,
            Detector carsDetector,
            DetectedCarProcessor carProcessor,
            DrawingService drawingService,
            ZoneCrossingService zoneCrossingService,
            LineCrossingService lineCrossingService,
            SpeedService speedService,
            @Qualifier("zoneConfigServiceImpl") ConfigService<Zone> zoneConfigService,
            CVShowing cvShowing,
            PolygonConfigService polygonConfigService,
            ImageScaleService scaleService) {
        super(dataOutputService, appConfigService, carsDetector, carProcessor, drawingService, zoneCrossingService, lineCrossingService, speedService, zoneConfigService, cvShowing, polygonConfigService, scaleService);
    }

    @Override
    protected void openCamera() {
        final String IP = appConfigService.findOne(ConfigAttribute.CameraIP).getValue();
        log.info("Try work with remote video from address: {}", IP);
        this.capture.open(IP);

        // is the video stream available?
        if (this.capture.isOpened()) {
            this.cameraActive = true;
            // start the video capture

            // update the button content
            button.setText("Stop Camera");

        } else {
            // log the error
            System.err.println("Impossible to open the camera connection...");
        }
    }

    @Override
    protected void stopCamera() {
        // update again the cameraButton content
        this.button.setText("Start Camera");
    }


}

