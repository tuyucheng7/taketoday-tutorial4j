package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Spring5Application.class)
class SpringContextTest {

	@Test
	void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}