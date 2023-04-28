package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.protobuf.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringContextTest {

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}
