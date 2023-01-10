package cn.tuyucheng.taketoday.controller;

import cn.tuyucheng.taketoday.model.Person;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonInfoController {

	@GetMapping("/person")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public @ResponseBody Person personInfo() {
		return new Person("abir", "Dhaka", "Bangladesh", 29, "Male");
	}
}
