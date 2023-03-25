package cn.tuyucheng.taketoday.quarkus.hello.sender.undetected;

import cn.tuyucheng.taketoday.quarkus.hello.service.HelloRetrieving;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class UndetectedHelloSender {

	public void sendHello(@Observes HelloRetrieving event) {
		event.getHelloReceiver().accept("Hi, I do not create a Jandex index, so I should not get detected.");
	}
}