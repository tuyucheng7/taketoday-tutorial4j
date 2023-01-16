package cn.tuyucheng.taketoday.test;

import cn.tuyucheng.taketoday.persistence.dao.UserRepository;
import cn.tuyucheng.taketoday.persistence.dao.VerificationTokenRepository;
import cn.tuyucheng.taketoday.persistence.model.User;
import cn.tuyucheng.taketoday.persistence.model.VerificationToken;
import cn.tuyucheng.taketoday.spring.LoginNotificationConfig;
import cn.tuyucheng.taketoday.spring.ServiceConfig;
import cn.tuyucheng.taketoday.spring.TestDbConfig;
import cn.tuyucheng.taketoday.spring.TestIntegrationConfig;
import cn.tuyucheng.taketoday.validation.EmailExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestDbConfig.class, ServiceConfig.class, TestIntegrationConfig.class, LoginNotificationConfig.class})
@Transactional
public class UserIntegrationTest {

	@Autowired
	private VerificationTokenRepository tokenRepository;

	@Autowired
	private UserRepository userRepository;

	@PersistenceContext
	private EntityManager entityManager;

	private Long tokenId;
	private Long userId;

	//

	@BeforeEach
	public void givenUserAndVerificationToken() throws EmailExistsException {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("SecretPassword");
		user.setFirstName("First");
		user.setLastName("Last");
		entityManager.persist(user);

		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken(token, user);
		entityManager.persist(verificationToken);

		entityManager.flush();
		entityManager.clear();

		tokenId = verificationToken.getId();
		userId = user.getId();
	}

	@AfterEach
	public void flushAfter() {
		entityManager.flush();
		entityManager.clear();
	}

	//

	@Test
	public void whenContextLoad_thenCorrect() {
		assertTrue(userRepository.count() > 0);
		assertTrue(tokenRepository.count() > 0);
	}

	// @Test(expected = Exception.class)
	@Test
	@Disabled("needs to go through the service and get transactional semantics")
	public void whenRemovingUser_thenFkViolationException() {
		userRepository.deleteById(userId);
	}

	@Test
	public void whenRemovingTokenThenUser_thenCorrect() {
		tokenRepository.deleteById(tokenId);
		userRepository.deleteById(userId);
	}

}
