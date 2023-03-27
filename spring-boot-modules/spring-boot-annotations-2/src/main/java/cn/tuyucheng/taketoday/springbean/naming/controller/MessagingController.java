package cn.tuyucheng.taketoday.springbean.naming.controller;

import cn.tuyucheng.taketoday.springbean.naming.service.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MessagingController {

	@Autowired
	private MessagingService service;
}
