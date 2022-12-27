package cn.tuyucheng.taketoday.boot.testing;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class EmployeeRepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
	void whenFindByName_thenReturnEmployee() {
		Employee alex = new Employee("alex");
		entityManager.persistAndFlush(alex);

		Employee found = employeeRepository.findByName(alex.getName());
		assertThat(found.getName()).isEqualTo(alex.getName());
	}

	@Test
	void whenInvalidName_thenReturnNull() {
		Employee fromDb = employeeRepository.findByName("doesNotExist");
		assertThat(fromDb).isNull();
	}

	@Test
	void whenFindById_thenReturnEmployee() {
		Employee emp = new Employee("test");
		entityManager.persistAndFlush(emp);

		Employee fromDb = employeeRepository.findById(emp.getId()).orElse(null);
		assertThat(fromDb.getName()).isEqualTo(emp.getName());
	}

	@Test
	void whenInvalidId_thenReturnNull() {
		Employee fromDb = employeeRepository.findById(-11L).orElse(null);
		assertThat(fromDb).isNull();
	}

	@Test
	void givenSetOfEmployees_whenFindAll_thenReturnAllEmployees() {
		Employee alex = new Employee("alex");
		Employee ron = new Employee("ron");
		Employee bob = new Employee("bob");

		entityManager.persist(alex);
		entityManager.persist(bob);
		entityManager.persist(ron);
		entityManager.flush();

		List<Employee> allEmployees = employeeRepository.findAll();

		Assertions.assertThat(allEmployees).hasSize(3).extracting(Employee::getName).containsOnly(alex.getName(), ron.getName(), bob.getName());
	}
}