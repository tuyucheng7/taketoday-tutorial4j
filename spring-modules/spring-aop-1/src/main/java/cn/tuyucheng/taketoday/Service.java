package cn.tuyucheng.taketoday;

import org.springframework.stereotype.Component;

@Component
public class Service {

	@LogExecutionTime
	public void serve() throws InterruptedException {
		Thread.sleep(2000);
	}
}