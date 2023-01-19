package cn.tuyucheng.taketoday.ejb.tutorial;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless(name = "HelloWorld")
public class HelloWorldBean implements HelloWorld {

	@Resource
	private SessionContext context;

	@Override
	public String getHelloWorld() {
		return "Welcome to EJB Tutorial!";
	}


}
