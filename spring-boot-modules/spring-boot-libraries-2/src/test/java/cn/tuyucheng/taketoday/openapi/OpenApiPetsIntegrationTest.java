package cn.tuyucheng.taketoday.openapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OpenApiApplication.class)
@AutoConfigureMockMvc
class OpenApiPetsIntegrationTest {

   private static final String PETS_PATH = "/pets/";

   @Autowired
   private MockMvc mockMvc;

   @Test
   void whenReadAll_thenStatusIsNotImplemented() throws Exception {
      this.mockMvc.perform(get(PETS_PATH))
            .andExpect(status().isNotImplemented());
   }

   @Test
   void whenReadOne_thenStatusIsNotImplemented() throws Exception {
      this.mockMvc.perform(get(PETS_PATH + 1))
            .andExpect(status().isNotImplemented());
   }
}