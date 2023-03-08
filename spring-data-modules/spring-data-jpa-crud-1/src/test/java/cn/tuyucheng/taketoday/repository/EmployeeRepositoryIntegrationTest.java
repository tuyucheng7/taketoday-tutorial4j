package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.boot.Application;
import cn.tuyucheng.taketoday.entity.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class EmployeeRepositoryIntegrationTest {
	private static final Employee EMPLOYEE1 = new Employee(1L, "John");
	private static final Employee EMPLOYEE2 = new Employee(2L, "Alice");

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
	void givenEmployeeEntity_whenInsertWithSave_ThenEmployeeIsPersisted() {
		employeeRepository.save(EMPLOYEE1);
		assertEmployeePersisted(EMPLOYEE1);
	}

	@Test
	void givenEmployeeEntity_whenInsertWithSaveAndFlush_ThenEmployeeIsPersisted() {
		employeeRepository.saveAndFlush(EMPLOYEE2);
		assertEmployeePersisted(EMPLOYEE2);
	}

	private void assertEmployeePersisted(Employee input) {
		Employee employee = employeeRepository.getById(input.getId());
		assertThat(employee).isNotNull();
	}
}