package cn.tuyucheng.taketoday.requestheader;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FooBarController {

   @GetMapping("foo")
   public String foo(HttpServletRequest request) {
      String operator = request.getHeader("operator");
      return "hello, " + operator;
   }

   @GetMapping("bar")
   public String bar(@RequestHeader("operator") String operator) {
      return "hello, " + operator;
   }
}
