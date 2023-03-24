package cn.tuyucheng.taketoday.aws.controller;

import cn.tuyucheng.taketoday.aws.data.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class ProfileController {

	@GetMapping(value = "users", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<User> getUser() {
		return List.of(
			new User("John", "Doe", "john.doe@baeldung.com"),
			new User("John", "Doe", "john.doe-2@baeldung.com"));
	}
}