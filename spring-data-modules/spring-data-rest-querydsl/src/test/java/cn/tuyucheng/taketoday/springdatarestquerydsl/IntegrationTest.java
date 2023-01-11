package cn.tuyucheng.taketoday.springdatarestquerydsl;

import cn.tuyucheng.taketoday.QueryDSLApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = QueryDSLApplication.class)
@WebAppConfiguration
class IntegrationTest {

    final MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype());

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void givenRequestHasBeenMade_whenQueryOverNameAttribute_thenGetJohn() throws Exception {
        // Get John
        mockMvc.perform(get("/users?name=John"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[0].address.address", is("Fake Street 1")))
                .andExpect(jsonPath("$[0].address.country", is("Spain")));
    }

    @Test
    void givenRequestHasBeenMade_whenQueryOverNameAttribute_thenGetLisa() throws Exception {
        // Get Lisa
        mockMvc.perform(get("/users?name=Lisa"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Lisa")))
                .andExpect(jsonPath("$[0].address.address", is("Real Street 1")))
                .andExpect(jsonPath("$[0].address.country", is("Germany")));
    }
}