package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.boot.daos.impl.PersonInsertRepository;
import cn.tuyucheng.taketoday.boot.domain.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(PersonInsertRepository.class)
class PersonInsertRepositoryIntegrationTest {
	private static final Long ID = 1L;
	private static final String FIRST_NAME = "firstname";
	private static final String LAST_NAME = "lastname";
	private static final Person PERSON = new Person(ID, FIRST_NAME, LAST_NAME);

	@Autowired
	private PersonInsertRepository personInsertRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void givenPersonEntity_whenInsertWithNativeQuery_ThenPersonIsPersisted() {
		insertWithQuery();

		assertPersonPersisted();
	}

	@Test
	void givenPersonEntity_whenInsertedTwiceWithNativeQuery_thenPersistenceExceptionExceptionIsThrown() {
		assertThatExceptionOfType(PersistenceException.class).isThrownBy(() -> {
			insertWithQuery();
			insertWithQuery();
		});
	}

	@Test
	void givenPersonEntity_whenInsertWithEntityManager_thenPersonIsPersisted() {
		insertPersonWithEntityManager();

		assertPersonPersisted();
	}

	@Test
	void givenPersonEntity_whenInsertedTwiceWithEntityManager_thenEntityExistsExceptionIsThrown() {
		assertThatExceptionOfType(EntityExistsException.class).isThrownBy(() -> {
			insertPersonWithEntityManager();
			insertPersonWithEntityManager();
		});
	}

	private void insertWithQuery() {
		personInsertRepository.insertWithQuery(PERSON);
	}

	private void insertPersonWithEntityManager() {
		personInsertRepository.insertWithEntityManager(new Person(ID, FIRST_NAME, LAST_NAME));
	}

	private void assertPersonPersisted() {
		Person person = entityManager.find(Person.class, ID);

		assertThat(person).isNotNull();
		assertThat(person.getId()).isEqualTo(PERSON.getId());
		assertThat(person.getFirstName()).isEqualTo(PERSON.getFirstName());
		assertThat(person.getLastName()).isEqualTo(PERSON.getLastName());
	}
}