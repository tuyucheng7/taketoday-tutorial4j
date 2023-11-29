package cn.tuyucheng.taketoday.controller.parameterized;

import cn.tuyucheng.taketoday.config.WebConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class RoleControllerIntegrationTest {

   private static final String CONTENT_TYPE = "application/text;charset=ISO-8859-1";
   @Autowired
   private WebApplicationContext wac;
   private MockMvc mockMvc;

   @Before
   public void setup() throws Exception {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
   }

   @Test
   public void givenEmployeeNameJohnWhenInvokeRoleThenReturnAdmin() throws Exception {
      this.mockMvc.perform(get("/role/John"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(CONTENT_TYPE))
            .andExpect(content().string("ADMIN"));
   }

   @Test
   public void givenEmployeeNameDoeWhenInvokeRoleThenReturnEmployee() throws Exception {
      this.mockMvc.perform(get("/role/Doe"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(CONTENT_TYPE))
            .andExpect(content().string("EMPLOYEE"));
   }
}