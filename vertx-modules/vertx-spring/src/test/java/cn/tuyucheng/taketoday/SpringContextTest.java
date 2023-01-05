package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.vertxspring.VertxSpringApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = VertxSpringApplication.class)
public class SpringContextTest {

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}