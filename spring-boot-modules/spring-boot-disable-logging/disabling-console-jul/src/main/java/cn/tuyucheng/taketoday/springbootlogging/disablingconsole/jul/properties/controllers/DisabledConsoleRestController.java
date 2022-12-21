package cn.tuyucheng.taketoday.springbootlogging.disablingconsole.jul.properties.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.logging.Logger;

@RestController
public class DisabledConsoleRestController {

    private static final Logger LOG = Logger.getLogger(DisabledConsoleRestController.class.getName());

    @GetMapping("/disabled-console-jul-properties")
    public String logTime() {
        LOG.info("Current time: " + LocalTime.now());
        return "finished!";
    }
}