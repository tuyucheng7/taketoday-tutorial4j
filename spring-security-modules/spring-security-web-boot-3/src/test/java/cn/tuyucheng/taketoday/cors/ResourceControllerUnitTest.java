package cn.tuyucheng.taketoday.cors;

import cn.tuyucheng.taketoday.cors.basicauth.SpringBootSecurityApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringBootSecurityApplication.class})
class ResourceControllerUnitTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
              .apply(SecurityMockMvcConfigurers.springSecurity())
              .build();
    }

    @Test
    void givenPreFlightRequest_whenPerformed_shouldReturnOK() throws Exception {
        mockMvc.perform(options("/user")
                    .header("Access-Control-Request-Method", "GET")
                    .header("Origin", "http://localhost:4200"))
              .andExpect(status().isOk());
    }
}