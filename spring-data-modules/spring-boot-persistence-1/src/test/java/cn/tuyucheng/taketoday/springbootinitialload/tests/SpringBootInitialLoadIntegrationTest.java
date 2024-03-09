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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@Sql(scripts = {"/employees_schema.sql", "/import_employees.sql"}, executionPhase = BEFORE_TEST_CLASS)
@Sql(scripts = {"/delete_employees_data.sql"}, executionPhase = AFTER_TEST_CLASS)
class SpringBootInitialLoadIntegrationTest {

   @Autowired
   private EmployeeRepository employeeRepository;

   @Test
   void testLoadDataForTestClass() {
      assertEquals(3, employeeRepository.findAll().size());
   }

   @Test
   @Sql(scripts = {"/import_senior_employees.sql"}, executionPhase = BEFORE_TEST_METHOD, config = @SqlConfig(encoding = "utf-8", transactionMode = TransactionMode.ISOLATED))
   void testLoadDataForTestCase() {
      assertEquals(5, employeeRepository.findAll().size());
   }
}