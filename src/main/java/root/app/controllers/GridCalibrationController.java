package root.app.controllers;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import root.app.controllers.request.GridUpdateRequest;
import root.app.data.services.CalibrationService;
import root.app.properties.ConfigAttribute;

import java.util.Arrays;

@Controller
@RequestMapping("grid")
public class GridCalibrationController {


    @Autowired
    private CalibrationService calibrationService;

    @PostMapping
    public ResponseEntity updateGrid(@RequestBody GridUpdateRequest updateRequest) {
        if (!valid(updateRequest)) {
            return ResponseEntity.badRequest().build();
        }

        calibrationService.fixPosition(updateRequest.getLineNumber(), updateRequest.getValue());

        return ResponseEntity.ok().build();
    }

    private boolean valid(GridUpdateRequest updateRequest) {
        return updateRequest.getValue() != null && Arrays.stream(ConfigAttribute.values()).anyMatch(v -> v.name().equals(updateRequest.getCalibrationType()));
    }
}
