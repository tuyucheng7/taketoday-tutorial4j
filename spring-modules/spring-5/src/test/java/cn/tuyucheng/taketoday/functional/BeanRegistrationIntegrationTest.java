package cn.tuyucheng.taketoday.functional;

import cn.tuyucheng.taketoday.Spring5Application;
import cn.tuyucheng.taketoday.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.support.GenericWebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Spring5Application.class)
class BeanRegistrationIntegrationTest {

	@Autowired
	private GenericWebApplicationContext context;

	@Test
	void whenRegisterBean_thenOk() {
		context.registerBean(MyService.class, MyService::new);
		MyService myService = (MyService) context.getBean("cn.tuyucheng.taketoday.functional.MyService");
		assertTrue(myService.getRandomNumber() < 10);
	}

	@Test
	void whenRegisterBeanWithName_thenOk() {
		context.registerBean("mySecondService", MyService.class, MyService::new);
		MyService mySecondService = (MyService) context.getBean("mySecondService");
		assertTrue(mySecondService.getRandomNumber() < 10);
	}

	@Test
	void whenRegisterBeanWithCallback_thenOk() {
		context.registerBean("myCallbackService", MyService.class,
			MyService::new, bd -> bd.setAutowireCandidate(false));
		MyService myCallbackService = (MyService) context.getBean("myCallbackService");
		assertTrue(myCallbackService.getRandomNumber() < 10);
	}
}