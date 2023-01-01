package cn.tuyucheng.taketoday.charencoding.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CharEncodingCheckController {

    @GetMapping("/ping")
    public String ping() {
        return "path";
    }
}