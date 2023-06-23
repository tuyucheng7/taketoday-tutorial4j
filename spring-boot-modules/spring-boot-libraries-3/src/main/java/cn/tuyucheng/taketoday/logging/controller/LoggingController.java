package cn.tuyucheng.taketoday.logging.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class LoggingController {

   @GetMapping("/{id}")
   public String findById(@PathVariable("id") Integer id) {
      return "Hello-" + id;
   }

   @PostMapping
   public String postWithId(@RequestBody Integer id) {
      return "Hello-" + id;
   }
}