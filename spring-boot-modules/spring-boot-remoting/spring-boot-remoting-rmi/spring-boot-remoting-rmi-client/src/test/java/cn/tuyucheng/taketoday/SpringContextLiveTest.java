package cn.tuyucheng.taketoday;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tuyucheng.taketoday.client.RmiClient;

/**
 * This Live Test requires:
 * * the `cn.tuyucheng.taketoday:spring-boot-remoting-http-api:jar:1.0.0` artifact accessible. For that we can run `mvn clean install` in the 'spring-boot-remoting/spring-boot-remoting-http/spring-boot-remoting-http-api' module.
 * * the 'spring-boot-remoting\spring-boot-remoting-rmi\spring-boot-remoting-rmi-server' service running
 */
@SpringBootTest(classes = RmiClient.class)
@RunWith(SpringRunner.class)
public class SpringContextLiveTest {

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}
