package root.app.data.runners.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.app.data.detectors.Detector;
import root.app.data.processors.DetectedCarProcessor;
import root.app.data.runners.BasicRunner;
import root.app.data.services.*;
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
            Detector carsDetector,
            DetectedCarProcessor carProcessor,
            DrawingService drawingService,
            ZoneCrossingService zoneCrossingService,
            LineCrossingService lineCrossingService,
            SpeedService speedService,
            LineConfigService lineProvider,
            CVShowing cvShowing) {
        super(carsDetector, carProcessor, drawingService, zoneCrossingService, lineCrossingService, speedService, lineProvider, cvShowing);
    }

    @Override
    protected void openCamera() {
        // start the video capture
        this.capture.open(pathToVideoFile);
        this.capture.set(CAP_PROP_FPS, 1);
        // update the button content
        this.button.setText("Stop Video");
    }

    @Override
    protected void stopCamera() {
        // update again the cameraButton content
        this.button.setText("Start Video");

    }

}

