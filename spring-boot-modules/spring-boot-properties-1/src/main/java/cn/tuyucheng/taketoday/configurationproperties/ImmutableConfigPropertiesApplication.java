package cn.tuyucheng.taketoday.configurationproperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ImmutableConfigPropertiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImmutableConfigPropertiesApplication.class, args);
	}
}