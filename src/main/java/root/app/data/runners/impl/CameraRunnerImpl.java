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
import root.app.properties.LineConfigService;

/**
 * Use video from camera
 */
@Component
@Slf4j
public class CameraRunnerImpl extends BasicRunner {

    @Autowired
    public CameraRunnerImpl(
            AppConfigService appConfigService,
            Detector carsDetector,
            DetectedCarProcessor carProcessor,
            DrawingService drawingService,
            ZoneCrossingService zoneCrossingService,
            LineCrossingService lineCrossingService,
            SpeedService speedService,
            LineConfigService lineProvider,
            CVShowing cvShowing) {
        super(appConfigService, carsDetector, carProcessor, drawingService, zoneCrossingService, lineCrossingService, speedService, lineProvider, cvShowing);
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
        stopCapturing();
        // the camera is not active at this point
        this.cameraActive = false;
        // update again the cameraButton content
        this.button.setText("Start Camera");
    }


}

