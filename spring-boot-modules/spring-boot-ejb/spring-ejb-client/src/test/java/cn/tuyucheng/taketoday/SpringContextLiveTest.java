package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.springejbclient.SpringEjbClientApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This Live Test requires:
 * * run the `spring-ejb-remote` module with the following command: `mvn clean package cargo:run -Pwildfly-standalone`
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringEjbClientApplication.class)
public class SpringContextLiveTest {

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}
