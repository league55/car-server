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

import static org.opencv.videoio.Videoio.CAP_PROP_FPS;
import static root.app.fx.controllers.MainController.pathToVideoFile;

/**
 * Use video from camera
 */
@Component
@Slf4j
public class VideoRunnerImpl extends BasicRunner {

    @Autowired
    public VideoRunnerImpl(
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
        // start the video capture
        final String filename = appConfigService.findOne(ConfigAttribute.PathToVideoFile).getValue();
        log.info("Try work with local video from file: {}", filename);
        this.capture.open(filename);
        this.capture.set(CAP_PROP_FPS, 30);
        // update the button content
        this.button.setText("Stop Video");
    }

    @Override
    protected void stopCamera() {
        // update again the cameraButton content
        this.button.setText("Start Video");

    }

}

