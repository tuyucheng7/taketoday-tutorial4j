package cn.tuyucheng.taketoday.lifecycleevents;

import cn.tuyucheng.taketoday.lifecycleevents.model.User;
import cn.tuyucheng.taketoday.lifecycleevents.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringBootLifecycleEventApplication.class)
class UserRepositoryIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setup() {
		User user = new User();
		user.setFirstName("Jane");
		user.setLastName("Smith");
		user.setUserName("jsmith123");
		userRepository.save(user);
	}

	@AfterEach
	void cleanup() {
		userRepository.deleteAll();
	}

	@Test
	void whenNewUserProvided_userIsAdded() {
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setUserName("jdoe123");
		user = userRepository.save(user);
		assertTrue(user.getId() > 0);
	}

	@Test
	void whenUserNameProvided_userIsLoaded() {
		User user = userRepository.findByUserName("jsmith123");
		assertNotNull(user);
		assertEquals("jsmith123", user.getUserName());
	}

	@Test
	void whenExistingUserProvided_userIsUpdated() {
		User user = userRepository.findByUserName("jsmith123");
		user.setFirstName("Joe");
		user = userRepository.save(user);
		assertEquals("Joe", user.getFirstName());
	}

	@Test
	void whenExistingUserDeleted_userIsDeleted() {
		User user = userRepository.findByUserName("jsmith123");
		userRepository.delete(user);
		user = userRepository.findByUserName("jsmith123");
		assertNull(user);
	}

	@Test
	void whenExistingUserLoaded_fullNameIsAvailable() {
		String expectedFullName = "Jane Smith";
		User user = userRepository.findByUserName("jsmith123");
		assertEquals(expectedFullName, user.getFullName());
	}
}