package root.app.data.runners.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import root.app.data.detectors.Detector;
import root.app.data.processors.DetectedCarProcessor;
import root.app.data.runners.BasicRunner;
import root.app.data.services.*;
import root.app.properties.LineConfigService;

/**
 * Use video from camera
 */
@Component
public class CameraRunnerImpl extends BasicRunner {

    @Autowired
    public CameraRunnerImpl(
            Detector carsDetector,
            DetectedCarProcessor carProcessor,
            DrawingService drawingService,
            LineCrossingService lineCrossingService,
            SpeedService speedService,
            LineConfigService lineProvider,
            ImageScaleService scaleService,
            CVShowing cvShowing) {
        super(carsDetector, carProcessor, drawingService, lineCrossingService, speedService, lineProvider, scaleService, cvShowing);
    }

    @Override
    protected void openCamera() {
        this.capture.open(cameraId);

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

