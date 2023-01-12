package cn.tuyucheng.taketoday.autowiring.controller;

import cn.tuyucheng.taketoday.autowiring.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CorrectController {

	@Autowired
	MyService myService;

	public String control() {
		return myService.serve();
	}

}
