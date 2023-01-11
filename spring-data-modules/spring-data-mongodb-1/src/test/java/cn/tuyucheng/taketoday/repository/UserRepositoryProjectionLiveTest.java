package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.config.MongoConfig;
import cn.tuyucheng.taketoday.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoConfig.class)
public class UserRepositoryProjectionLiveTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoOperations mongoOps;

	@Before
	public void testSetup() {
		if (!mongoOps.collectionExists(User.class)) {
			mongoOps.createCollection(User.class);
		}
	}

	@After
	public void tearDown() {
		mongoOps.dropCollection(User.class);
	}

	@Test
	public void givenUserExists_whenAgeZero_thenSuccess() {
		mongoOps.insert(new User("John", 30));
		mongoOps.insert(new User("Ringo", 35));

		userRepository.findNameAndId()
			.forEach(user -> {
				assertNotNull(user.getName());
				assertTrue(user.getAge().equals(0));
			});
	}

	@Test
	public void givenUserExists_whenIdNull_thenSuccess() {
		mongoOps.insert(new User("John", 30));
		mongoOps.insert(new User("Ringo", 35));

		userRepository.findNameAndAgeExcludeId()
			.forEach(user -> {
				assertNull(user.getId());
				assertNotNull(user.getAge());
			});
	}

}
