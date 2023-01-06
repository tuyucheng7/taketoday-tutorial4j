package cn.tuyucheng.taketoday.springbootdatasourceconfig.application.tests;

import cn.tuyucheng.taketoday.springbootdatasourceconfig.application.entities.User;
import cn.tuyucheng.taketoday.springbootdatasourceconfig.application.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void whenCalledSave_thenCorrectNumberOfUsers() {
		userRepository.save(new User("Bob", "bob@domain.com"));
		List<User> users = (List<User>) userRepository.findAll();

		// 2 additional users are saved in the CommandLineRunner bean
		assertThat(users.size()).isEqualTo(3);
	}
}