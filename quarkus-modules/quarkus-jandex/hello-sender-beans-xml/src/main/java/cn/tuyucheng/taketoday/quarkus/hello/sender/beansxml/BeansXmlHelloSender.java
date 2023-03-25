package cn.tuyucheng.taketoday.quarkus.hello.sender.beansxml;

import cn.tuyucheng.taketoday.quarkus.hello.service.HelloRetrieving;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class BeansXmlHelloSender {

	public void sendHello(@Observes HelloRetrieving event) {
		event.getHelloReceiver().accept("Hi, I was detected using an empty META-INF/beans.xml file.");
	}
}