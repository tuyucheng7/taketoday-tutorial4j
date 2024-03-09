package cn.tuyucheng.taketoday.spring.jdbc.template.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class EmployeeDAOUnitTest {
   @Mock
   JdbcTemplate jdbcTemplate;

   DataSource dataSource;

   @BeforeEach
   void setup() {
      dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
            .generateUniqueName(true)
            .addScript("classpath:cn/tuyucheng/taketoday/spring/jdbc/template/testing/schema.sql")
            .addScript("classpath:cn/tuyucheng/taketoday/spring/jdbc/template/testing/test-data.sql")
            .build();
   }

   @Test
   void whenMockJdbcTemplate_thenReturnCorrectEmployeeCount() {
      EmployeeDAO employeeDAO = new EmployeeDAO();
      ReflectionTestUtils.setField(employeeDAO, "jdbcTemplate", jdbcTemplate);
      Mockito.when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM EMPLOYEE", Integer.class))
            .thenReturn(4);

      assertEquals(4, employeeDAO.getCountOfEmployees());

      Mockito.when(jdbcTemplate.queryForObject(Mockito.anyString(), Mockito.eq(Integer.class)))
            .thenReturn(3);

      assertEquals(3, employeeDAO.getCountOfEmployees());
   }

   @Test
   void whenInjectInMemoryDataSource_thenReturnCorrectEmployeeCount() {
      EmployeeDAO employeeDAO = new EmployeeDAO();
      employeeDAO.setDataSource(dataSource);

      assertEquals(4, employeeDAO.getCountOfEmployees());
   }

   @Test
   void whenIdNotExist_thenThrowEmptyResultDataAccessException() {
      EmployeeDAO employeeDAO = new EmployeeDAO();
      ReflectionTestUtils.setField(employeeDAO, "jdbcTemplate", jdbcTemplate);
      Mockito.when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Employee>>any(), anyInt()))
            .thenThrow(EmptyResultDataAccessException.class);

      assertThrows(EmptyResultDataAccessException.class, () -> employeeDAO.getEmployeeById(1));
   }

   @Test
   void whenIdNotExist_thenReturnNull() {
      EmployeeDAO employeeDAO = new EmployeeDAO();
      ReflectionTestUtils.setField(employeeDAO, "jdbcTemplate", jdbcTemplate);
      Mockito.when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Employee>>any(), anyInt()))
            .thenReturn(null);

      assertNull(employeeDAO.getEmployeeByIdV2(1));
   }
}