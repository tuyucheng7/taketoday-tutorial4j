package cn.tuyucheng.taketoday.test;

import cn.tuyucheng.taketoday.persistence.dao.UserRepository;
import cn.tuyucheng.taketoday.persistence.dao.VerificationTokenRepository;
import cn.tuyucheng.taketoday.persistence.model.User;
import cn.tuyucheng.taketoday.persistence.model.VerificationToken;
import cn.tuyucheng.taketoday.spring.TestDbConfig;
import cn.tuyucheng.taketoday.spring.TestTaskConfig;
import cn.tuyucheng.taketoday.task.TokensPurgeTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestDbConfig.class, TestTaskConfig.class})
@Transactional
public class TokenExpirationIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VerificationTokenRepository tokenRepository;

	@Autowired
	private TokensPurgeTask tokensPurgeTask;

	@PersistenceContext
	private EntityManager entityManager;

	private Long token_id;
	private Long user_id;

	//

	@BeforeEach
	public void givenUserWithExpiredToken() {

		// we need a clear token repository
		tokenRepository.deleteAll();

		User user = new User();
		user.setEmail(UUID.randomUUID().toString() + "@example.com");
		user.setPassword(UUID.randomUUID().toString());
		user.setFirstName("First");
		user.setLastName("Last");

		entityManager.persist(user);
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken(token, user);
		verificationToken.setExpiryDate(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)));

		entityManager.persist(verificationToken);

        /*
            flush managed entities to the database to populate identifier field
         */
		entityManager.flush();

        /*
            remove managed entities from the persistence context
            so that subsequent SQL queries hit the database
         */
		entityManager.clear();

		token_id = verificationToken.getId();
		user_id = user.getId();
	}

	@Test
	public void whenContextLoad_thenCorrect() {
		assertNotNull(user_id);
		assertNotNull(token_id);
		assertTrue(userRepository.findById(user_id).isPresent());

		Optional<VerificationToken> verificationToken = tokenRepository.findById(token_id);
		assertTrue(verificationToken.isPresent());
		assertTrue(tokenRepository.findAllByExpiryDateLessThan(Date.from(Instant.now())).anyMatch((token) -> token.equals(verificationToken.get())));
	}

	@AfterEach
	public void flushAfter() {
		entityManager.flush();
	}

	@Test
	public void whenRemoveByGeneratedQuery_thenCorrect() {
		tokenRepository.deleteByExpiryDateLessThan(Date.from(Instant.now()));
		assertEquals(0, tokenRepository.count());
	}

	@Test
	public void whenRemoveByJPQLQuery_thenCorrect() {
		tokenRepository.deleteAllExpiredSince(Date.from(Instant.now()));
		assertEquals(0, tokenRepository.count());
	}

	@Test
	public void whenPurgeTokenTask_thenCorrect() {
		tokensPurgeTask.purgeExpired();
		assertFalse(tokenRepository.findById(token_id).isPresent());
	}
}
