package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.annotationconfigvscomponentscan.components.AccountService;
import cn.tuyucheng.taketoday.annotationconfigvscomponentscan.components.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringXMLConfigurationIntegrationTest {

	@Test
	public void givenContextAnnotationConfigOrContextComponentScan_whenDependenciesAndBeansAnnotated_thenNoXMLNeeded() {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:annotationconfigvscomponentscan-beans.xml");
		UserService userService = context.getBean(UserService.class);
		AccountService accountService = context.getBean(AccountService.class);
		Assert.assertNotNull(userService);
		Assert.assertNotNull(accountService);
		Assert.assertNotNull(userService.getAccountService());
	}

}
