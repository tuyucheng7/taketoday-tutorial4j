package cn.tuyucheng.taketoday.boot.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/hello")
public class WebController {

   private String name;

   @GetMapping
   public String salutation() {
      return "Hello " + Optional.ofNullable(name).orElse("world") + '!';
   }

   @PutMapping
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public void setName(@RequestBody final String name) {
      this.name = name;
   }

   @DeleteMapping
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public void resetToDefault() {
      this.name = null;
   }
}