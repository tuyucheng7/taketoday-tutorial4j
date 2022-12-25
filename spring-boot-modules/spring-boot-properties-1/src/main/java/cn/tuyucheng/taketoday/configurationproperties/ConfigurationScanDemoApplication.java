package cn.tuyucheng.taketoday.configurationproperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("cn.tuyucheng.taketoday.configurationproperties")
public class ConfigurationScanDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigurationScanDemoApplication.class, args);
	}
}