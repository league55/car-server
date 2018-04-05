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


/**
 * Use video from camera
 */
@Component
@Slf4j
public class VideoRunnerImpl extends BasicRunner {

    @Autowired
    public VideoRunnerImpl(
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
        // start the video capture
        final String filename = appConfigService.findOne(ConfigAttribute.PathToVideoFile).getValue();
        log.info("Try work with local video from file: {}", filename);
        this.capture.open(filename);
        // update the button content
    }

}

