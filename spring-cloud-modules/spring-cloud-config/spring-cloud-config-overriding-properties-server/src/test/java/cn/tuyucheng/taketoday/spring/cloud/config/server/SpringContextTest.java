package cn.tuyucheng.taketoday.spring.cloud.config.server;

import cn.tuyucheng.taketoday.spring.cloud.config.overridingproperties.ConfigServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ConfigServer.class)
@ActiveProfiles("native")
class SpringContextTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}