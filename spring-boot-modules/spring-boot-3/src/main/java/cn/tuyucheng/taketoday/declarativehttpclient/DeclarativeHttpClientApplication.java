package cn.tuyucheng.taketoday.declarativehttpclient;

import cn.tuyucheng.taketoday.declarativehttpclient.model.User;
import cn.tuyucheng.taketoday.declarativehttpclient.web.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class DeclarativeHttpClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DeclarativeHttpClientApplication.class, args);
	}

	@Autowired
	UserClient userClient;

	@Override
	public void run(String... args) throws Exception {
		// Get All Users
		userClient.getAll().subscribe(
			data -> LOGGER.info("User: {}", data)
		);

		// Get User By Id
		userClient.getById(1L).subscribe(
			data -> LOGGER.info("User: {}", data)
		);

		// Create a New User
		userClient.save(new User(null, "Lokesh", "lokesh", "admin@email.com"))
			.log()
			/*.flatMap(user -> {
					  var uri = user.getHeaders().getLocation().toString();
					  var strings = uri.split("/");
					  return userClient.getById(Long.valueOf(strings[strings.length - 1]))
						  .log()
						  .map(u -> {
							  LOGGER.debug("User: {}", u);
							  return u;
						  });
				  }
			  )*/.subscribe(
				data -> LOGGER.info("User: {}", data)
			);

		// Delete User By Id
		userClient.delete(1L).subscribe(
			data -> LOGGER.info("User: {}", data)
		);
	}
}