package cn.tuyucheng.taketoday.configurationproperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {Database.class, DatabaseConfig.class})
public class DatabaseConfigPropertiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabaseConfigPropertiesApplication.class, args);
	}
}