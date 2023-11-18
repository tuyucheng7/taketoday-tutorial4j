package cn.tuyucheng.taketoday.overridebean.mockbean;

import cn.tuyucheng.taketoday.overridebean.Endpoint;
import cn.tuyucheng.taketoday.overridebean.Service;
import cn.tuyucheng.taketoday.overridebean.boot.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Application.class, Endpoint.class})
@AutoConfigureMockMvc
class MockBeanIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   @MockBean
   private Service service;

   @Test
   void givenServiceMockBean_whenGetHelloEndpoint_thenMockOk() throws Exception {
      when(service.helloWorld()).thenReturn("hello mock bean");
      this.mockMvc.perform(get("/hello"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("hello mock bean")));
   }
}