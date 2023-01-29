package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.amqp.SpringWebfluxAmqpApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This live test requires:
 * rabbitmq instance running on the environment
 *
 * <br>
 * To run rabbitmq using docker image:
 * (e.g. `docker run -d --name rabbitmq -p 5672:5672 rabbitmq:3`)
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringWebfluxAmqpApplication.class)
class SpringContextLiveTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}