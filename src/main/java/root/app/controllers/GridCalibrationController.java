package root.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import root.app.controllers.request.GridUpdateRequest;
import root.app.data.services.CalibrationService;
import root.app.properties.ConfigAttribute;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("grid")
public class GridCalibrationController {
    private final List<CalibrationService> calibrationServices;

    @Autowired
    public GridCalibrationController(List<CalibrationService> calibrationServices) {
        this.calibrationServices = calibrationServices;
    }

    @PostMapping
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity updateGrid(@RequestBody GridUpdateRequest updateRequest) {
        if (!valid(updateRequest)) {
            return ResponseEntity.badRequest().build();
        }

        getCalibrationService(updateRequest.getCalibrationType()).fixPosition(updateRequest.getLineNumber(), updateRequest.getValue());

        return ResponseEntity.ok().build();
    }

    private boolean valid(GridUpdateRequest updateRequest) {
        return updateRequest.getValue() != null && Arrays.stream(ConfigAttribute.values()).anyMatch(v -> v.name().equals(updateRequest.getCalibrationType()));
    }

    private CalibrationService getCalibrationService(String calType) {
        return calibrationServices
                .stream()
                .filter(service -> service.canCalibrate(calType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Wrong calibration Type"));
    }
}
