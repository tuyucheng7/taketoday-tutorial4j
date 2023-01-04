package cn.tuyucheng.taketoday.unsupportedmediatype;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping(value = "/")
    List<User> getAllUsers() {
        return Collections.singletonList(new User(1, "Andy", 28, "14th Street"));
    }

    @GetMapping(value = "/{user-id}")
    User getUser(@PathVariable("user-id") Integer userId) {
        return new User(userId, "Andy", 28, "14th Street");
    }

    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    void AddUser(@RequestBody User user) {
        // Adding the User in the repository
    }
}