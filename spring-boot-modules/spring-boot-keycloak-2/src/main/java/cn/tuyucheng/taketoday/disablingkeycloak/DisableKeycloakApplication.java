package cn.tuyucheng.taketoday.disablingkeycloak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.tuyucheng.taketoday.disablingkeycloak"})
public class DisableKeycloakApplication {

	public static void main(String[] args) {
		SpringApplication.run(DisableKeycloakApplication.class, args);
	}
}