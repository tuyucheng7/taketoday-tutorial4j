package cn.tuyucheng.taketoday.spring.cloud.config.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ConfigServer.class)
@WebAppConfiguration
class SpringContextTest {
	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}