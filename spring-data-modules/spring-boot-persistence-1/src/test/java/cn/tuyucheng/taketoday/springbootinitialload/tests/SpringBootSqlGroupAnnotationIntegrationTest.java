package cn.tuyucheng.taketoday.springbootinitialload.tests;

import cn.tuyucheng.taketoday.boot.Application;
import cn.tuyucheng.taketoday.boot.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@SqlGroup({
	@Sql(scripts = "/employees_schema.sql", config = @SqlConfig(transactionMode = TransactionMode.ISOLATED)),
	@Sql("/import_employees.sql")}
)
class SpringBootSqlGroupAnnotationIntegrationTest {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
	void testLoadDataForTestCase() {
		assertEquals(3, employeeRepository.findAll().size());
	}
}