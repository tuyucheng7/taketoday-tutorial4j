package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.springbootlogging.SpringBootLoggingApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringBootLoggingApplication.class)
class SpringContextTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}