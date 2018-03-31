package root.app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/ping")
public class Ping {

    @GetMapping
    public String ping() {
        return "pong";
    }
}
