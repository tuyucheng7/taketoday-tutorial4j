package cn.tuyucheng.taketoday.spring.servicevalidation.controller;

import cn.tuyucheng.taketoday.spring.servicevalidation.domain.UserAccount;
import cn.tuyucheng.taketoday.spring.servicevalidation.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAccountController {

	@Autowired
	private UserAccountService service;

	@PostMapping("/addUserAccount")
	public Object addUserAccount(@RequestBody UserAccount userAccount) {
		return service.addUserAccount(userAccount);
	}
}