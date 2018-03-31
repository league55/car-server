package root.app.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import root.app.model.AppConfigDTO;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class SettingsController {
    @Autowired
    private AppConfigService appConfigService;

    @PostMapping("settings")
    public void updateProperty(@RequestBody List<AppConfigDTO> appConfig) {
        Validate.notEmpty(appConfig);
        appConfig.forEach(c -> appConfigService.save(c));
    }

    @GetMapping("settings")
    @CrossOrigin(origins = "http://localhost:3000")
    public List<AppConfigDTO> loadProps() {
        log.info("Loaded all config");
        return Arrays.stream(ConfigAttribute.values()).map(key -> appConfigService.findOne(key)).collect(toList());
    }
}
