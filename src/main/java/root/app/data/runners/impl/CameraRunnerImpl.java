package root.app.data.runners.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.app.data.detectors.Detector;
import root.app.data.processors.DetectedCarProcessor;
import root.app.data.runners.BasicRunner;
import root.app.data.services.*;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;
import root.app.properties.PolygonConfigService;
import root.app.properties.RoadWaysConfigService;

;

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
            RoadWaysConfigService zoneConfigService,
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
            cameraActive = true;

        } else {
            // log the error
            System.err.println("Impossible to open the camera connection...");
        }
    }


}

