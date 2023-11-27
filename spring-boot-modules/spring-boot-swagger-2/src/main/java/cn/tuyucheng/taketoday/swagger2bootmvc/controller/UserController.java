package cn.tuyucheng.taketoday.swagger2bootmvc.controller;

import cn.tuyucheng.taketoday.swagger2bootmvc.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

   public UserController() {
      super();
   }

   @RequestMapping(method = RequestMethod.POST, value = "/createUser", produces = "application/json; charset=UTF-8")
   @ResponseStatus(HttpStatus.CREATED)
   @ResponseBody
   @Operation(summary = "Create user",
         description = "This method creates a new user")
   public User createUser(@Parameter(
         name = "firstName",
         description = "First Name of the user",
         example = "Vatsal",
         required = true) @RequestParam String firstName) {
      return new User(firstName);
   }
}