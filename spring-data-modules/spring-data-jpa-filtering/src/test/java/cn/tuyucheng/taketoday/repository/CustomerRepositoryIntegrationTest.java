package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.entity.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class CustomerRepositoryIntegrationTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private CustomerRepository repository;

	@BeforeEach
	void before() {
		entityManager.persist(new Customer("A", "A@example.com"));
		entityManager.persist(new Customer("D", null));
		entityManager.persist(new Customer("D", "D@example.com"));
		entityManager.persist(new Customer("C", null, UUID.fromString("c7c19ff4-8636-4b99-9591-c3327652f191")));
	}

	@Test
	void givenQueryMethod_whenEmailIsNull_thenFoundByNullEmail() {
		List<Customer> customers = repository.findByNameAndEmail("D", null);

		assertEquals(1, customers.size());
		Customer actual = customers.get(0);

		assertNull(actual.getEmail());
		assertEquals("D", actual.getName());
	}

	@Test
	void givenQueryMethod_whenEmailIsAbsent_thenIgnoreEmail() {
		List<Customer> customers = repository.findByName("D");

		assertEquals(2, customers.size());
	}

	@Test
	void givenQueryAnnotation_whenEmailIsNull_thenIgnoreEmail() {
		List<Customer> customers = repository.findCustomerByNameAndEmail("D", null);

		assertEquals(2, customers.size());
	}

	@Test
	public void givenUUIDIsPresent_whenQueryMethod_thenFetchedCorrectly() {
		List<Customer> customers = repository.findCustomerByUuid(UUID.fromString("c7c19ff4-8636-4b99-9591-c3327652f191"));

		assertEquals(1, customers.size());
	}

	@Test
	public void givenNullUuid_whenQueryMethod_thenFetchedCorrectly() {
		List<Customer> customers = repository.findCustomerByUuid(null);

		assertEquals(3, customers.size());
	}

	@AfterEach
	void cleanUp() {
		repository.deleteAll();
	}
}