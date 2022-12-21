package cn.tuyucheng.taketoday.displayallbeans.controller;

import cn.tuyucheng.taketoday.displayallbeans.service.FooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class FooController {
    @Autowired
    private FooService fooService;

    @GetMapping(value = "/displayallbeans")
    public ResponseEntity<String> getHeaderAndBody(Map<String, Object> model) {
        model.put("header", fooService.getHeader());
        model.put("message", fooService.getBody());
        return ResponseEntity.ok("displayallbeans");
    }
}