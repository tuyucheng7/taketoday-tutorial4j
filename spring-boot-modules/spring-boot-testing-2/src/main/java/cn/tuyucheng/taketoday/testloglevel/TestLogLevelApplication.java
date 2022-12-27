package cn.tuyucheng.taketoday.testloglevel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.tuyucheng.taketoday.testloglevel", "cn.tuyucheng.taketoday.component"})
public class TestLogLevelApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestLogLevelApplication.class, args);
	}
}