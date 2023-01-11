package cn.tuyucheng.taketoday.global.exceptionhandler.controller;

import cn.tuyucheng.taketoday.global.exceptionhandler.handler.RestResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LoginController {

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> login() {
        return ResponseEntity.ok(new RestResponse("Success"));
    }

    @PostMapping(value = "/login-handler", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> loginWithExceptionHandler() {
        return ResponseEntity.ok(new RestResponse("Success"));
    }

}
