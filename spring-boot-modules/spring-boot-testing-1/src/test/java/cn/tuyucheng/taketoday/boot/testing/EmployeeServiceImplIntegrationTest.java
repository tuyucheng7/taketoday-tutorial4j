package cn.tuyucheng.taketoday.boot.testing;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class EmployeeServiceImplIntegrationTest {

	@TestConfiguration
	static class EmployeeServiceImplTestContextConfiguration {
		@Bean
		EmployeeService employeeService() {
			return new EmployeeServiceImpl();
		}
	}

	@Autowired
	private EmployeeService employeeService;

	@MockBean
	private EmployeeRepository employeeRepository;

	@BeforeEach
	void setUp() {
		Employee john = new Employee("john");
		john.setId(11L);

		Employee bob = new Employee("bob");
		Employee alex = new Employee("alex");

		List<Employee> allEmployees = Arrays.asList(john, bob, alex);

		Mockito.when(employeeRepository.findByName(john.getName())).thenReturn(john);
		Mockito.when(employeeRepository.findByName(alex.getName())).thenReturn(alex);
		Mockito.when(employeeRepository.findByName("wrong_name")).thenReturn(null);
		Mockito.when(employeeRepository.findById(john.getId())).thenReturn(Optional.of(john));
		Mockito.when(employeeRepository.findAll()).thenReturn(allEmployees);
		Mockito.when(employeeRepository.findById(-99L)).thenReturn(Optional.empty());
	}

	@Test
	void whenValidName_thenEmployeeShouldBeFound() {
		String name = "alex";
		Employee found = employeeService.getEmployeeByName(name);

		assertThat(found.getName()).isEqualTo(name);
	}

	@Test
	void whenInValidName_thenEmployeeShouldNotBeFound() {
		Employee fromDb = employeeService.getEmployeeByName("wrong_name");
		assertThat(fromDb).isNull();

		verifyFindByNameIsCalledOnce("wrong_name");
	}

	@Test
	void whenValidName_thenEmployeeShouldExist() {
		boolean doesEmployeeExist = employeeService.exists("john");
		assertThat(doesEmployeeExist).isTrue();

		verifyFindByNameIsCalledOnce("john");
	}

	@Test
	void whenNonExistingName_thenEmployeeShouldNotExist() {
		boolean doesEmployeeExist = employeeService.exists("some_name");
		assertThat(doesEmployeeExist).isFalse();

		verifyFindByNameIsCalledOnce("some_name");
	}

	@Test
	void whenValidId_thenEmployeeShouldBeFound() {
		Employee fromDb = employeeService.getEmployeeById(11L);
		assertThat(fromDb.getName()).isEqualTo("john");

		verifyFindByIdIsCalledOnce();
	}

	@Test
	void whenInValidId_thenEmployeeShouldNotBeFound() {
		Employee fromDb = employeeService.getEmployeeById(-99L);
		verifyFindByIdIsCalledOnce();
		assertThat(fromDb).isNull();
	}

	@Test
	void given3Employees_whengetAll_thenReturn3Records() {
		Employee alex = new Employee("alex");
		Employee john = new Employee("john");
		Employee bob = new Employee("bob");

		List<Employee> allEmployees = employeeService.getAllEmployees();
		verifyFindAllEmployeesIsCalledOnce();
		Assertions.assertThat(allEmployees).hasSize(3).extracting(Employee::getName).contains(alex.getName(), john.getName(), bob.getName());
	}

	private void verifyFindByNameIsCalledOnce(String name) {
		Mockito.verify(employeeRepository, VerificationModeFactory.times(1)).findByName(name);
		Mockito.reset(employeeRepository);
	}

	private void verifyFindByIdIsCalledOnce() {
		Mockito.verify(employeeRepository, VerificationModeFactory.times(1)).findById(Mockito.anyLong());
		Mockito.reset(employeeRepository);
	}

	private void verifyFindAllEmployeesIsCalledOnce() {
		Mockito.verify(employeeRepository, VerificationModeFactory.times(1)).findAll();
		Mockito.reset(employeeRepository);
	}
}