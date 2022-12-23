package cn.tuyucheng.taketoday.annotations;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ConditionalBeanConfiguration {

	@Conditional(HibernateCondition.class)
	Properties additionalProperties() {
		// application specific properties
		return new Properties();
	}
}