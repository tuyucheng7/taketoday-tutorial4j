package cn.tuyucheng.taketoday.beanvalidation.application;

import cn.tuyucheng.taketoday.beanvalidation.application.controllers.UserController;
import cn.tuyucheng.taketoday.beanvalidation.application.repositories.UserRepository;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

	@MockBean
	private UserRepository userRepository;

	@Autowired
	UserController userController;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void whenUserControllerInjected_thenNotNull() throws Exception {
		assertThat(userController).isNotNull();
	}

	@Test
	void whenGetRequestToUsers_thenCorrectResponse() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/users")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

	}

	@Test
	void whenPostRequestToUsersAndValidUser_thenCorrectResponse() throws Exception {
		MediaType textPlainUtf8 = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
		String user = "{\"name\": \"bob\", \"email\" : \"bob@domain.com\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
				.content(user)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(textPlainUtf8));
	}

	@Test
	void whenPostRequestToUsersAndInValidUser_thenCorrectResponse() throws Exception {
		String user = "{\"name\": \"\", \"email\" : \"bob@domain.com\"}";
		mockMvc.perform(MockMvcRequestBuilders.post("/users")
				.content(user)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.jsonPath("$.name", Is.is("Name is mandatory")))
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
	}
}