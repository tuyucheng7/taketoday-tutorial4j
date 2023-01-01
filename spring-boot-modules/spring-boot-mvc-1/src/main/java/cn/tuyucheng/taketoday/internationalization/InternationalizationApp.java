package cn.tuyucheng.taketoday.internationalization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.security.RolesAllowed;

@SpringBootApplication
public class InternationalizationApp {
	@RolesAllowed("*")
	public static void main(String[] args) {
		SpringApplication.run(InternationalizationApp.class, args);
	}
}