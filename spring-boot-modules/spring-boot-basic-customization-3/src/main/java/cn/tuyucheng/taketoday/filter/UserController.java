package cn.tuyucheng.taketoday.filter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class UserController {

   @GetMapping("/users")
   public List<User> getUsers() {
      return Arrays.asList(new User("1", "John", "john@email.com"),
            new User("2", "Smith", "smith@email.com"));
   }
}