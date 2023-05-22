package cn.tuyucheng.taketoday;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

   @GetMapping("/")
   public String greeting() {
      return "Hello World";
   }
}