package cn.tuyucheng.taketoday.quarkus.hello.sender.applicationproperties;

import cn.tuyucheng.taketoday.quarkus.hello.service.HelloRetrieving;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class ApplicationPropertiesHelloSender {

	public void sendHello(@Observes HelloRetrieving event) {
		event.getHelloReceiver().accept("Hi, I was detected by inserting this module's groupId and artifactId into the app's application.properties file.");
	}
}