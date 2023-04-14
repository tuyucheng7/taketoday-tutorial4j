package cn.tuyucheng.taketoday.services;

import cn.tuyucheng.taketoday.requestresponsebody.LoginForm;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {

	public boolean fakeAuthenticate(LoginForm lf) {
		return true;
	}
}