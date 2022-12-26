package cn.tuyucheng.taketoday.integrationtesting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SecuredController.class)
class SecuredControllerWebMvcIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Test
	void givenRequestOnPrivateService_shouldFailWith401() throws Exception {
		mvc.perform(get("/private/hello")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@WithMockUser(value = "spring")
	@Test
	void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
		mvc.perform(get("/private/hello")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
}