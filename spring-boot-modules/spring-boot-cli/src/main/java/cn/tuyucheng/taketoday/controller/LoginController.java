package cn.tuyucheng.taketoday.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

   @GetMapping("/")
   public String hello() {
      return "Hello World!";
   }
}