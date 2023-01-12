package com.tuyucheng.taketoday.dubbo.remote;

public class GreetingsServiceImpl implements GreetingsService {

	@Override
	public String sayHi(String name) {
		System.out.println("default implementation");
		return "hi, " + name;
	}
}