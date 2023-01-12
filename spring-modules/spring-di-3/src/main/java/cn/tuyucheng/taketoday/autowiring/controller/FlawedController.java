package cn.tuyucheng.taketoday.autowiring.controller;

import cn.tuyucheng.taketoday.autowiring.service.MyService;
import org.springframework.stereotype.Controller;

@Controller
public class FlawedController {

	public String control() {
		MyService userService = new MyService();
		return userService.serve();
	}

}
