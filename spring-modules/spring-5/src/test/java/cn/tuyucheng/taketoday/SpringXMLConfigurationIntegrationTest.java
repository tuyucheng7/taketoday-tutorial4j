package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.annotationconfigvscomponentscan.components.AccountService;
import cn.tuyucheng.taketoday.annotationconfigvscomponentscan.components.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpringXMLConfigurationIntegrationTest {

	@Test
	void givenContextAnnotationConfigOrContextComponentScan_whenDependenciesAndBeansAnnotated_thenNoXMLNeeded() {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:annotationconfigvscomponentscan-beans.xml");
		UserService userService = context.getBean(UserService.class);
		AccountService accountService = context.getBean(AccountService.class);

		assertNotNull(userService);
		assertNotNull(accountService);
		assertNotNull(userService.getAccountService());
	}
}