package cn.tuyucheng.taketoday.properties.external;

import cn.tuyucheng.taketoday.properties.spring.PropertiesWithJavaConfigOther;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ExternalPropertiesWithJavaConfig.class, PropertiesWithJavaConfigOther.class}, loader = AnnotationConfigContextLoader.class)
@Disabled("manual only")
public class ExternalPropertiesWithJavaIntegrationTest {

	@Autowired
	private Environment env;

	@Value("${key.something}")
	private String injectedProperty;

	@Value("${external.something}")
	private String injectedExternalProperty;

	@Test
	final void givenContextIsInitialized_thenNoException() {
		System.out.println("in test via @Value: " + injectedProperty);
		System.out.println("in test Environment: " + env.getProperty("key.something"));

		System.out.println("in test via @Value - external: " + injectedExternalProperty);
		System.out.println("in test Environment - external: " + env.getProperty("external.something"));
	}
}