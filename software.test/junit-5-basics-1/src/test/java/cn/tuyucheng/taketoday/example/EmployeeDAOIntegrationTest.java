package cn.tuyucheng.taketoday.example;

import cn.tuyucheng.taketoday.junit.tags.example.Employee;
import cn.tuyucheng.taketoday.junit.tags.example.EmployeeDAO;
import cn.tuyucheng.taketoday.junit.tags.example.SpringJdbcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringJdbcConfig.class}, loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class EmployeeDAOIntegrationTest {

   @Autowired
   private EmployeeDAO employeeDao;

   @Test
   public void testQueryMethod() {
      Assert.assertEquals(employeeDao.getAllEmployees().size(), 4);
   }

   @Test
   public void testUpdateMethod() {
      Assert.assertEquals(employeeDao.addEmployee(5), 1);
   }

   @Test
   public void testAddEmployeeUsingSimpleJdbcInsert() {
      final Employee emp = new Employee();
      emp.setId(11);
      emp.setFirstName("testFirstName");
      emp.setLastName("testLastName");
      emp.setAddress("testAddress");

      Assert.assertEquals(1, employeeDao.addEmployeeUsingSimpleJdbcInsert(emp));
   }
}