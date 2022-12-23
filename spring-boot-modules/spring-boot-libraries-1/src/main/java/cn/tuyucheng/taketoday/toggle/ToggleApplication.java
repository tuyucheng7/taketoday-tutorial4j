package cn.tuyucheng.taketoday.toggle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.security.RolesAllowed;

@SpringBootApplication
public class ToggleApplication {

	@RolesAllowed("*")
	public static void main(String[] args) {
		SpringApplication.run(ToggleApplication.class, args);
	}
}