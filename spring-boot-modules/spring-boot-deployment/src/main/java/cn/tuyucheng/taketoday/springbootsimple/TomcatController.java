package cn.tuyucheng.taketoday.springbootsimple;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.IntStream;

@RestController
public class TomcatController {

   @GetMapping(value = "/hello")
   public Collection<String> sayHello() {
      return IntStream.range(0, 10)
            .mapToObj(i -> STR."Hello number \{i}")
            .toList();
   }
}