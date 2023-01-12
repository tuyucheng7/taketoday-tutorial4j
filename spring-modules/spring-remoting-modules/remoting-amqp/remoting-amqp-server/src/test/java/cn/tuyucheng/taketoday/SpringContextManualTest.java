package cn.tuyucheng.taketoday;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tuyucheng.taketoday.server.AmqpServer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AmqpServer.class)
public class SpringContextManualTest {

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}
