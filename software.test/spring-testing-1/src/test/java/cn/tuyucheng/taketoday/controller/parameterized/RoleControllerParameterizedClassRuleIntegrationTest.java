package cn.tuyucheng.taketoday.controller.parameterized;

import cn.tuyucheng.taketoday.config.WebConfig;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(Parameterized.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class RoleControllerParameterizedClassRuleIntegrationTest {

   @ClassRule
   public static final SpringClassRule scr = new SpringClassRule();
   private static final String CONTENT_TYPE = "application/text;charset=ISO-8859-1";
   @Rule
   public final SpringMethodRule smr = new SpringMethodRule();
   @Parameter(value = 0)
   public String name;
   @Parameter(value = 1)
   public String role;
   @Autowired
   private WebApplicationContext wac;
   private MockMvc mockMvc;

   @Parameters
   public static Collection<Object[]> data() {
      Collection<Object[]> params = new ArrayList<>();
      params.add(new Object[]{"John", "ADMIN"});
      params.add(new Object[]{"Doe", "EMPLOYEE"});
      return params;
   }

   @Before
   public void setup() throws Exception {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
   }

   @Test
   public void givenEmployeeNameWhenInvokeRoleThenReturnRole() throws Exception {
      this.mockMvc.perform(get("/role/" + name))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(CONTENT_TYPE))
            .andExpect(content().string(role));
   }
}