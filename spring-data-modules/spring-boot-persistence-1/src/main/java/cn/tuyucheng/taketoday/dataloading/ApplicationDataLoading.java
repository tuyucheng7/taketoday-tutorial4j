package cn.tuyucheng.taketoday.dataloading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication(scanBasePackages = {"cn.tuyucheng.taketoday.dataloading"})
public class ApplicationDataLoading {

	public static void main(String[] args) {
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "datasource");

		SpringApplication.run(ApplicationDataLoading.class, args);
	}
}