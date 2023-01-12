package com.tuyucheng.taketoday.dubbo.remote;

public class GreetingsFailoverServiceImpl implements GreetingsService {

	@Override
	public String sayHi(String name) {
		System.out.println("failover implementation");
		return "hi, failover " + name;
	}
}