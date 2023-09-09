package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.servicevalidation.SpringServiceLayerValidationApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SpringServiceLayerValidationApplication.class)
class SpringContextTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}