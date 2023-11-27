package cn.tuyucheng.taketoday.encoding;

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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PasswordEncodingUnitTest {
   private final static String userName = "tuyucheng";
   private final static String passwordDecoded = "tuyuchengPassword";

   private MockMvc mvc;

   @Autowired
   private WebApplicationContext webApplicationContext;

   @BeforeEach
   void setup() {
      mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
   }

   @Test
   void givenRequestWithWrongPassword_shouldFailWith401() throws Exception {
      mvc.perform(get("/").with(httpBasic(userName, "wrongPassword")))
            .andExpect(status().isUnauthorized());
   }

   @Test
   void givenRequestWithCorrectDecodedPassword_shouldSucceedWith200() throws Exception {
      mvc.perform(get("/").with(httpBasic(userName, passwordDecoded)))
            .andExpect(status().isOk());
   }
}