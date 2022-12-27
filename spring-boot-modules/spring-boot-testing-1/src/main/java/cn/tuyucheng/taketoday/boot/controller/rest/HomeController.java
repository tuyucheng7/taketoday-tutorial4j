package cn.tuyucheng.taketoday.boot.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public String salutation() {
		return "Welcome !";
	}
}