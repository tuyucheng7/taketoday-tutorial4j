package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.boot.Application;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = Application.class)
class SpringJpaContextIntegrationTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}