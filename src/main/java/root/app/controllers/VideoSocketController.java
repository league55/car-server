package root.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import root.app.data.runners.BasicRunner;

@Controller
public class VideoSocketController {


    @MessageMapping("/")
    public void greeting(String message) throws Exception {
    }

}

