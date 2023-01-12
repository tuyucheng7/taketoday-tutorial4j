package cn.tuyucheng.taketoday.properties.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:configForProperties.xml")
@ComponentScan("cn.tuyucheng.taketoday.core")
public class PropertiesWithXmlConfig {

	public PropertiesWithXmlConfig() {
		super();
	}

}