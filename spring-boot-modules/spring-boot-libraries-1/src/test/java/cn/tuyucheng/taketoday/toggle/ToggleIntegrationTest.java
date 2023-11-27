package cn.tuyucheng.taketoday.toggle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ToggleApplication.class)
@AutoConfigureMockMvc
class ToggleIntegrationTest {

   @Autowired
   private EmployeeRepository employeeRepository;

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private WebApplicationContext wac;

   @BeforeEach
   void setup() {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
   }

   @Test
   void givenFeaturePropertyFalse_whenIncreaseSalary_thenNoIncrease() throws Exception {
      Employee emp = new Employee(1, 2000);
      employeeRepository.save(emp);

      System.setProperty("employee.feature", "false");

      mockMvc.perform(post("/increaseSalary").param("id", STR."\{emp.getId()}")).andExpect(status().is(200));

      emp = employeeRepository.findById(1L).orElse(null);
      assertEquals(2000, emp.getSalary(), 0.5, "salary incorrect");
   }

   @Test
   void givenFeaturePropertyTrue_whenIncreaseSalary_thenIncrease() throws Exception {
      Employee emp = new Employee(1, 2000);
      employeeRepository.save(emp);

      System.setProperty("employee.feature", "true");

      mockMvc.perform(post("/increaseSalary").param("id", STR."\{emp.getId()}")).andExpect(status().is(200));

      emp = employeeRepository.findById(1L).orElse(null);
      assertEquals(2200, emp.getSalary(), 0.5, "salary incorrect");
   }
}