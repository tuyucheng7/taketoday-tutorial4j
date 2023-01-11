package cn.tuyucheng.taketoday.springdatawebsupport.application;

import cn.tuyucheng.taketoday.springdatawebsupport.application.entities.User;
import cn.tuyucheng.taketoday.springdatawebsupport.application.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class SpringDataWebSupportApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataWebSupportApplication.class, args);
	}

	@Bean
	CommandLineRunner initialize(UserRepository userRepository) {
		return args -> {
			Stream.of("John", "Robert", "Nataly", "Helen", "Mary").forEach(name -> {
				User user = new User(name);
				userRepository.save(user);
			});
			userRepository.findAll().forEach(System.out::println);
		};
	}
}