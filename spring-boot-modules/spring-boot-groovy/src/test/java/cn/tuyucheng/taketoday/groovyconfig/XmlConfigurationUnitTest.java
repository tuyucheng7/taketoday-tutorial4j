package cn.tuyucheng.taketoday.groovyconfig;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlConfigurationUnitTest {

	@Test
	void whenXmlConfig_thenCorrectPerson() {
		final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("xml-bean-config.xml");

		JavaPersonBean j = (JavaPersonBean) applicationContext.getBean("JavaPersonBean");

		assertEquals("30", j.getAge());
		assertEquals("brown", j.getEyesColor());
		assertEquals("brown", j.getHairColor());
	}
}