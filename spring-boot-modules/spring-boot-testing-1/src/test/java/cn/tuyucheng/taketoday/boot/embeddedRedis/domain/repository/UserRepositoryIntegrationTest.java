package cn.tuyucheng.taketoday.boot.embeddedRedis.domain.repository;

import cn.tuyucheng.taketoday.boot.embeddedRedis.TestRedisConfiguration;
import cn.tuyucheng.taketoday.boot.embeddedRedis.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
class UserRepositoryIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void shouldSaveUser_toRedis() {
		final UUID id = UUID.randomUUID();
		final User user = new User(id, "name");

		final User saved = userRepository.save(user);

		assertNotNull(saved);
	}
}