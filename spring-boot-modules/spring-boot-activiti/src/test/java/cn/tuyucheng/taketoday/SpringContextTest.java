package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.activitiwithspring.ActivitiWithSpringApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ActivitiWithSpringApplication.class)
@AutoConfigureTestDatabase
class SpringContextTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}