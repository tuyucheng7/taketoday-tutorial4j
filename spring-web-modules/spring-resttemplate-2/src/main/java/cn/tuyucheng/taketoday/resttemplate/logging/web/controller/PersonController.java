package cn.tuyucheng.taketoday.resttemplate.logging.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class PersonController {

    @PostMapping("/persons")
    public List<String> getPersons() {
        return Arrays.asList("Lucie", "Jackie", "Danesh", "Tao");
    }
}