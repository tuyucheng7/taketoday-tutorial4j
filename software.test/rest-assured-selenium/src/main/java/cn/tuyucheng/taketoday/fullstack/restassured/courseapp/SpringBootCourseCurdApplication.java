package cn.tuyucheng.taketoday.fullstack.restassured.courseapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class SpringBootCourseCurdApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootCourseCurdApplication.class, args);
	}
}