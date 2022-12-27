package cn.tuyucheng.taketoday.swaggerenums.controller;

import cn.tuyucheng.taketoday.swaggerenums.model.Employee;
import cn.tuyucheng.taketoday.swaggerenums.model.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HireControllerUnitTest {

	@Test
	void givenRoleEngineer_whenHireEmployee_thenReturnsRoleInString() {
		// Arrange
		Role testRole = Role.Engineer;
		Employee employee = new Employee();
		employee.setRole(testRole);

		// Act
		HireController hireController = new HireController();
		String response = hireController.hireEmployee(employee);

		// Assert
		Assertions.assertEquals(String.format("Hired for role: %s", testRole), response);
	}
}