package cn.tuyucheng.taketoday.activiti.security.withspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.tuyucheng.taketoday.activiti.security.config", "cn.tuyucheng.taketoday.activiti.security.withspring"})
public class SpringSecurityActivitiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityActivitiApplication.class, args);
	}
}