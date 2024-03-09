package cn.tuyucheng.taketoday.spring.jdbc.template.guide;

import cn.tuyucheng.taketoday.spring.jdbc.template.guide.config.SpringJdbcConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringJdbcConfig.class}, loader = AnnotationConfigContextLoader.class)
class EmployeeDAOIntegrationTest {

   private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeDAOIntegrationTest.class);

   @Autowired
   private EmployeeDAO employeeDao;

   @Test
   void testGetCountOfEmployees() {
      Assertions.assertEquals(employeeDao.getCountOfEmployees(), 9);
   }

   @Test
   void testQueryMethod() {
      Assertions.assertEquals(employeeDao.getAllEmployees().size(), 4);
   }

   @Test
   void testUpdateMethod() {
      Assertions.assertEquals(employeeDao.addEmplyee(5), 1);
   }

   @Test
   void testAddEmployeeUsingSimpelJdbcInsert() {
      final Employee emp = new Employee();
      emp.setId(11);
      emp.setFirstName("testFirstName");
      emp.setLastName("testLastName");
      emp.setAddress("testAddress");

      Assertions.assertEquals(employeeDao.addEmplyeeUsingSimpelJdbcInsert(emp), 1);
   }

   @Test
   void testExecuteMethod() {
      employeeDao.addEmplyeeUsingExecuteMethod();
   }

   @Test
   void testMapSqlParameterSource() {
      Assertions.assertEquals("James", employeeDao.getEmployeeUsingMapSqlParameterSource());
   }

   @Test
   void testBeanPropertySqlParameterSource() {
      Assertions.assertEquals(1, employeeDao.getEmployeeUsingBeanPropertySqlParameterSource());
   }

   @Test
   void testCustomExceptionTranslator() {
      employeeDao.addEmplyee(7);

      try {
         employeeDao.addEmplyee(7);
      } catch (final DuplicateKeyException e) {
         LOGGER.error(e.getMessage(), e);
         Assertions.assertTrue(e.getMessage().contains("Custome Exception translator - Integrity contraint voilation."));
      }
   }

   @Test
   void testBatchUpdateUsingJDBCTemplate() {
      final List<Employee> employees = new ArrayList<>();
      final Employee emp1 = new Employee();
      emp1.setId(10);
      emp1.setFirstName("firstName1");
      emp1.setLastName("lastName1");
      emp1.setAddress("address1");

      final Employee emp2 = new Employee();
      emp2.setId(20);
      emp2.setFirstName("firstName2");
      emp2.setLastName("lastName2");
      emp2.setAddress("address2");

      final Employee emp3 = new Employee();
      emp3.setId(30);
      emp3.setFirstName("firstName3");
      emp3.setLastName("lastName3");
      emp3.setAddress("address3");

      employees.add(emp1);
      employees.add(emp2);
      employees.add(emp3);

      employeeDao.batchUpdateUsingJDBCTemplate(employees);

      Assertions.assertNotNull(employeeDao.getEmployee(10));
      Assertions.assertNotNull(employeeDao.getEmployee(20));
      Assertions.assertNotNull(employeeDao.getEmployee(30));
   }

   @Test
   void testBatchUpdateUsingNamedParameterJDBCTemplate() {
      final List<Employee> employees = new ArrayList<>();
      final Employee emp1 = new Employee();
      emp1.setId(40);
      emp1.setFirstName("firstName4");
      emp1.setLastName("lastName4");
      emp1.setAddress("address4");

      final Employee emp2 = new Employee();
      emp2.setId(50);
      emp2.setFirstName("firstName5");
      emp2.setLastName("lastName5");
      emp2.setAddress("address5");

      final Employee emp3 = new Employee();
      emp3.setId(60);
      emp3.setFirstName("firstName6");
      emp3.setLastName("lastName6");
      emp3.setAddress("address6");

      employees.add(emp1);
      employees.add(emp2);
      employees.add(emp3);

      employeeDao.batchUpdateUsingNamedParameterJDBCTemplate(employees);

      Assertions.assertNotNull(employeeDao.getEmployee(40));
      Assertions.assertNotNull(employeeDao.getEmployee(50));
      Assertions.assertNotNull(employeeDao.getEmployee(60));
   }
}