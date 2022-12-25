package cn.tuyucheng.taketoday.properties.external;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.properties.core")
@PropertySource("classpath:foo.properties")
public class ExternalPropertiesWithJavaConfig {

	public ExternalPropertiesWithJavaConfig() {
		super();
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}