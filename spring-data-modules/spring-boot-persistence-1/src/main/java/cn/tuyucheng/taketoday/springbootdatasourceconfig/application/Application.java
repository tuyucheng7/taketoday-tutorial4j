package cn.tuyucheng.taketoday.springbootdatasourceconfig.application;

import cn.tuyucheng.taketoday.springbootdatasourceconfig.application.entities.User;
import cn.tuyucheng.taketoday.springbootdatasourceconfig.application.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner run(UserRepository userRepository) {
		return (String[] args) -> {
			User user1 = new User("John", "john@domain.com");
			User user2 = new User("Julie", "julie@domain.com");
			userRepository.save(user1);
			userRepository.save(user2);
			userRepository.findAll().forEach(user -> System.out.println(user.getName()));
		};
	}
}