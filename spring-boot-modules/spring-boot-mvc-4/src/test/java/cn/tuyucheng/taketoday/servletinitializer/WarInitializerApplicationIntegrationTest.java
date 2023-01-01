package cn.tuyucheng.taketoday.servletinitializer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = WarInitializerApplication.WarInitializerController.class)
class WarInitializerApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenContextRootUrlIsAccessed_thenStatusIsOk() throws Exception {
        mockMvc.perform(get("/"))
              .andExpect(status().is(200));
    }

    @Test
    void whenContextRootUrlIsAccesed_thenCorrectStringIsReturned() throws Exception {
        mockMvc.perform(get("/"))
              .andExpect(content().string(containsString("WarInitializerApplication is up and running!")));
    }
}