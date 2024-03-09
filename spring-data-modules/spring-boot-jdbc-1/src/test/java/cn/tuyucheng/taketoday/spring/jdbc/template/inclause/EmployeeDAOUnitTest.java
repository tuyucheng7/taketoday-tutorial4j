package cn.tuyucheng.taketoday.spring.jdbc.template.inclause;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmployeeDAOUnitTest {
   @Mock
   JdbcTemplate jdbcTemplate;

   DataSource dataSource;

   @BeforeEach
   void setup() {
      dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
            .generateUniqueName(true)
            .addScript("classpath:cn/tuyucheng/taketoday/spring/jdbc/template/inclause/schema.sql")
            .addScript("classpath:cn/tuyucheng/taketoday/spring/jdbc/template/inclause/test-data.sql")
            .build();
   }

   @Test
   void givenSmallIdList_whenGetEmployeesFromIdList_thenReturnCorrectEmployees() {
      List<Integer> ids = new ArrayList<>();
      ids.add(1);
      ids.add(3);
      ids.add(4);
      EmployeeDAO employeeDAO = new EmployeeDAO();
      employeeDAO.setDataSource(dataSource);

      List<Employee> employees = employeeDAO.getEmployeesFromIdList(ids);

      assertEquals(3, employees.size());
      assertEquals(1, employees.get(0).getId());
      assertEquals(3, employees.get(1).getId());
      assertEquals(4, employees.get(2).getId());

      employees = employeeDAO.getEmployeesFromIdListNamed(ids);

      assertEquals(3, employees.size());
      assertEquals(1, employees.get(0).getId());
      assertEquals(3, employees.get(1).getId());
      assertEquals(4, employees.get(2).getId());
   }

   @Test
   void givenLargeIdList_whenGetEmployeesFromIdList_thenReturnCorrectEmployees() {
      List<Integer> ids = new ArrayList<>();
      ids.add(1);
      ids.add(3);
      ids.add(4);
      EmployeeDAO employeeDAO = new EmployeeDAO();
      employeeDAO.setDataSource(dataSource);

      List<Employee> employees = employeeDAO.getEmployeesFromLargeIdList(ids);

      assertEquals(3, employees.size());
      assertEquals(1, employees.get(0).getId());
      assertEquals(3, employees.get(1).getId());
      assertEquals(4, employees.get(2).getId());

      ids.clear();
      ids.add(2);
      employees = employeeDAO.getEmployeesFromLargeIdList(ids);
      assertEquals(1, employees.size());
   }
}