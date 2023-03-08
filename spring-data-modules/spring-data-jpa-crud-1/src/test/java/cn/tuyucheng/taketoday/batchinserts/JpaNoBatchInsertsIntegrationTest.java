package cn.tuyucheng.taketoday.batchinserts;

import cn.tuyucheng.taketoday.boot.Application;
import cn.tuyucheng.taketoday.batchinserts.model.School;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static cn.tuyucheng.taketoday.batchinserts.TestObjectHelper.createSchool;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional
@ActiveProfiles("batchinserts")
@TestPropertySource(properties = "spring.jpa.properties.hibernate.jdbc.batch_size=-1")
class JpaNoBatchInsertsIntegrationTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void whenNotConfigured_ThenSendsInsertsSeparately() {
		for (int i = 0; i < 10; i++) {
			School school = createSchool(i);
			entityManager.persist(school);
		}
	}

	@AfterEach
	void tearDown() {
		entityManager.flush();
	}
}