package cn.tuyucheng.taketoday.overridebean.profile;

import cn.tuyucheng.taketoday.overridebean.Endpoint;
import cn.tuyucheng.taketoday.overridebean.Service;
import cn.tuyucheng.taketoday.overridebean.boot.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Application.class, ProfileConfig.class, Endpoint.class, ProfileTestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("mock")
class ProfileMockIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private Service service;

   @Test
   void givenConfigurationWithProfile_whenTestProfileIsActive_thenMockOk() throws Exception {
      when(service.helloWorld()).thenReturn("hello profile mock");
      this.mockMvc.perform(get("/hello"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("hello profile mock")));
   }
}