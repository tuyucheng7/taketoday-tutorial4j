package cn.tuyucheng.taketoday.springbean;

import cn.tuyucheng.taketoday.springbean.domain.Company;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpringBeanIntegrationTest {

	@Test
	void whenUsingIoC_thenDependenciesAreInjected() {
		ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		Company company = context.getBean("company", Company.class);
		assertEquals("High Street", company.getAddress().getStreet());
		assertEquals(1000, company.getAddress().getNumber());
	}
}