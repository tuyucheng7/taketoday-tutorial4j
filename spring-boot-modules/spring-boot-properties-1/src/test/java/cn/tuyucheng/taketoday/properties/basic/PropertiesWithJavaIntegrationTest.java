package cn.tuyucheng.taketoday.properties.basic;

import cn.tuyucheng.taketoday.properties.spring.PropertiesWithJavaConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PropertiesWithJavaConfig.class}, loader = AnnotationConfigContextLoader.class)
class PropertiesWithJavaIntegrationTest {

	@Autowired
	private Environment env;

	@Value("${key.something}")
	private String injectedProperty;

	@Test
	final void givenContextIsInitialized_thenNoException() {
		System.out.println("in test via @Value: " + injectedProperty);
		System.out.println("in test Environment: " + env.getProperty("key.something"));
	}
}