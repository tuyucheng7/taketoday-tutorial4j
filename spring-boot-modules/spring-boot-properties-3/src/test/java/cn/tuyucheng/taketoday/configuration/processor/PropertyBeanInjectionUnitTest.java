package cn.tuyucheng.taketoday.configuration.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestPropertySource("/configuration-processor.properties")
@SpringBootTest(classes = DemoApplication.class)
class PropertyBeanInjectionUnitTest {

	@Autowired
	private PropertyBeanInjection propertyBeanInjection;

	@Test
	void checkThatJdbcPropertiesHaveTheCorrectValueFromPropertiesFile() {
		assertEquals("jdbc:postgresql:/localhost:5432", propertyBeanInjection.getJdbcUrl());
	}

	@Test
	void checkThatCustomPropertiesHaveTheCorrectValueFromPropertiesFile() {
		assertEquals("www.abc.test.com", propertyBeanInjection.getUrl());
		assertEquals(2000, propertyBeanInjection.getTimeoutInMilliseconds());
	}
}