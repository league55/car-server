package root.app.controllers;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import root.app.data.services.ApplicationStateService;
import root.app.data.services.DrawingService;
import root.app.model.CalibrationDTO;
import root.app.model.MarkersPair;
import root.app.model.Point;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CalibrationController {

    @Autowired
    private DrawingService drawingService;

    @Autowired
    private ApplicationStateService applicationStateService;

    @PostMapping("calibration")
    public void submitZones(@RequestBody CalibrationDTO points) {
        Validate.notNull(points);
        drawingService.submitRegion(new MarkersPair(points));
    }

    @DeleteMapping("calibration")
    @CrossOrigin(origins = "http://localhost:3000")
    public void resetZones() {
        applicationStateService.resetZones();
    }
}
