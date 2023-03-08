package cn.tuyucheng.taketoday.batchinserts;

import cn.tuyucheng.taketoday.boot.Application;
import cn.tuyucheng.taketoday.batchinserts.model.School;
import cn.tuyucheng.taketoday.batchinserts.model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

import static cn.tuyucheng.taketoday.batchinserts.TestObjectHelper.createSchool;
import static cn.tuyucheng.taketoday.batchinserts.TestObjectHelper.createStudent;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Transactional
@ActiveProfiles("batchinserts")
class JpaBatchInsertsIntegrationTest {

	@PersistenceContext
	private EntityManager entityManager;

	private static final int BATCH_SIZE = 5;

	@Transactional
	@Test
	void whenInsertingSingleTypeOfEntity_thenCreatesSingleBatch() {
		for (int i = 0; i < 10; i++) {
			School school = createSchool(i);
			entityManager.persist(school);
		}
	}

	@Transactional
	@Test
	void whenFlushingAfterBatch_ThenClearsMemory() {
		for (int i = 0; i < 10; i++) {
			if (i > 0 && i % BATCH_SIZE == 0) {
				entityManager.flush();
				entityManager.clear();
			}

			School school = createSchool(i);
			entityManager.persist(school);
		}
	}

	@Transactional
	@Test
	void whenThereAreMultipleEntities_ThenCreatesNewBatch() {
		for (int i = 0; i < 10; i++) {
			if (i > 0 && i % BATCH_SIZE == 0) {
				entityManager.flush();
				entityManager.clear();
			}

			School school = createSchool(i);
			entityManager.persist(school);
			Student firstStudent = createStudent(school);
			Student secondStudent = createStudent(school);
			entityManager.persist(firstStudent);
			entityManager.persist(secondStudent);
		}
	}

	@Transactional
	@Test
	void whenUpdatingEntities_thenCreatesBatch() {
		for (int i = 0; i < 10; i++) {
			School school = createSchool(i);
			entityManager.persist(school);
		}

		entityManager.flush();

		TypedQuery<School> schoolQuery = entityManager.createQuery("SELECT s from School s", School.class);
		List<School> allSchools = schoolQuery.getResultList();

		for (School school : allSchools) {
			school.setName("Updated_" + school.getName());
		}
	}

	@AfterEach
	void tearDown() {
		entityManager.flush();
	}
}