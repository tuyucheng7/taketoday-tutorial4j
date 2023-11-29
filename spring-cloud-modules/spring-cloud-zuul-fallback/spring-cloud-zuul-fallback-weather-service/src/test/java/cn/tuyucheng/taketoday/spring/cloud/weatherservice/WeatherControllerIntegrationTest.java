package cn.tuyucheng.taketoday.spring.cloud.weatherservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WeatherController.class)
class WeatherControllerIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   @Test
   void whenWeatherControllerInvoked_thenReturnWeatherInformation() throws Exception {
      this.mockMvc.perform(get("/weather/today"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("bright sunny day")));
   }
}