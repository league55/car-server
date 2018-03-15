package root.app.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.app.data.runners.BasicRunner;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@Slf4j
public class VideoController {

    @Qualifier("videoRunnerImpl")
    @Autowired
    private BasicRunner videoRunner;

    @RequestMapping("/video/{cameraId}")
    public String getVideoStream(@PathVariable String cameraId) {
        return "blabla" + cameraId;
    }

}
