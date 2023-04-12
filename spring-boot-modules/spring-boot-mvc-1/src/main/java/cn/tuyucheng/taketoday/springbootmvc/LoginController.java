package cn.tuyucheng.taketoday.springbootmvc;

import cn.tuyucheng.taketoday.springbootmvc.model.LoginForm;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/")
public class LoginController {

	@PostMapping("loginform")
	public String processLogin(@Valid @RequestBody LoginForm form) {
		return "Success";
	}
}