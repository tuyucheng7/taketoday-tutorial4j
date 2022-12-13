package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.extensions.EmployeeDaoParameterResolver;
import cn.tuyucheng.taketoday.extensions.EmployeeDatabaseSetupExtension;
import cn.tuyucheng.taketoday.extensions.EnvironmentExtension;
import cn.tuyucheng.taketoday.helpers.Employee;
import cn.tuyucheng.taketoday.helpers.EmployeeJdbcDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.SQLException;

@ExtendWith({EnvironmentExtension.class, EmployeeDaoParameterResolver.class})
class ProgrammaticEmployeesUnitTest {

	private final EmployeeJdbcDao employeeDao;

	@RegisterExtension
	static EmployeeDatabaseSetupExtension DB = new EmployeeDatabaseSetupExtension("jdbc:h2:mem:AnotherDb;DB_CLOSE_DELAY=-1", "org.h2.Driver", "sa", "");

	ProgrammaticEmployeesUnitTest(EmployeeJdbcDao employeeDao) {
		this.employeeDao = employeeDao;
	}

	@Test
	void whenAddEmployee_thenGetEmployee() throws SQLException {
		Employee emp = new Employee(1, "john");
		employeeDao.add(emp);
		Assertions.assertEquals(1, employeeDao.findAll().size());
	}

	@Test
	void whenGetEmployees_thenEmptyList() throws SQLException {
		Assertions.assertEquals(0, employeeDao.findAll().size());
	}
}