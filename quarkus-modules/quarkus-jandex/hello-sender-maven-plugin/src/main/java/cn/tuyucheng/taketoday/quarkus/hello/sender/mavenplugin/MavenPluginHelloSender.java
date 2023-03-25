package cn.tuyucheng.taketoday.quarkus.hello.sender.mavenplugin;

import cn.tuyucheng.taketoday.quarkus.hello.service.HelloRetrieving;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class MavenPluginHelloSender {

	public void sendHello(@Observes HelloRetrieving event) {
		event.getHelloReceiver().accept("Hi, I was detected using the Jandex Maven Plugin.");
	}
}