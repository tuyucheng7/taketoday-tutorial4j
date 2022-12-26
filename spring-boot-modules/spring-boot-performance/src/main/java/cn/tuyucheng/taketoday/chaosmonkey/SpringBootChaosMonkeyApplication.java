package cn.tuyucheng.taketoday.chaosmonkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.tuyucheng.taketoday.chaosmonkey"})
public class SpringBootChaosMonkeyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootChaosMonkeyApplication.class, args);
	}
}