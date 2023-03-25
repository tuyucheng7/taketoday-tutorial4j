package cn.tuyucheng.taketoday.dynamicvalidation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.security.RolesAllowed;

@SpringBootApplication
public class DynamicValidationApp {
	@RolesAllowed("*")
	public static void main(String[] args) {
		SpringApplication.run(DynamicValidationApp.class, args);
	}
}
