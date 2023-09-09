package cn.tuyucheng.taketoday;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TuyuchengController {

   @GetMapping("/hello")
   public String sayHello() {
      return "hello";
   }

   @PostMapping("/tuyucheng")
   public String sayHelloPost() {
      return "hello";
   }
}