package root.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import root.app.data.runners.BasicRunner;

@RestController
public class StatusController {
    private final BasicRunner videoRunner;

    @Autowired
    public StatusController(@Qualifier("videoRunnerImpl") BasicRunner videoRunner) {
        this.videoRunner = videoRunner;
    }

    @GetMapping("/status")
    @CrossOrigin(origins = "http://localhost:3000")
    public Boolean getCameraStatus() {
        return videoRunner.isRunning();
    }
}
