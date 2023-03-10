package cn.tuyucheng.taketoday.springbootcrudapp.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "cn.tuyucheng.taketoday.springbootcrudapp.application.repositories")
@EnableTransactionManagement
@EntityScan(basePackages = "cn.tuyucheng.taketoday.springbootcrudapp.application.entities")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}