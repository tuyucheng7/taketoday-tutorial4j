package cn.tuyucheng.taketoday.ddd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "cn.tuyucheng.taketoday.ddd.order")
public class PersistingDddAggregatesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersistingDddAggregatesApplication.class, args);
	}
}