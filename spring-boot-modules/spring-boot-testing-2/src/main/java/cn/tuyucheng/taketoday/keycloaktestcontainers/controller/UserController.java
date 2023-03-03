package cn.tuyucheng.taketoday.keycloaktestcontainers.controller;

import cn.tuyucheng.taketoday.keycloaktestcontainers.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

	@GetMapping("me")
	public UserDto getMe() {
		return new UserDto(1L, "janedoe", "Doe", "Jane", "jane.doe@baeldung.com");
	}
}