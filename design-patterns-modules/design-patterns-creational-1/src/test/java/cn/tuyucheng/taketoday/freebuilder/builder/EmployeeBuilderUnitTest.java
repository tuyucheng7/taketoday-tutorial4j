package cn.tuyucheng.taketoday.freebuilder.builder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class EmployeeBuilderUnitTest {

	public static final String NAME = "tuyucheng";

	@Test
	void whenBuildEmployee_thenReturnValidEmployee() {
		// when
		Employee.Builder emplBuilder = new Employee.Builder();

		Employee employee = emplBuilder
			.setName(NAME)
			.setAge(12)
			.setDepartment("Builder Pattern")
			.build();

		// then
		Assertions.assertTrue(employee.getName().equalsIgnoreCase(NAME));
	}
}