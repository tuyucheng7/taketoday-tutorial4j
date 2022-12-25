package cn.tuyucheng.taketoday.buildproperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.buildproperties")
@PropertySource("classpath:build.properties")
//@PropertySource("classpath:build.yml")
public class BuildInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuildInfoApplication.class, args);
	}
}