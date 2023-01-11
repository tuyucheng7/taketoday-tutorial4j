package cn.tuyucheng.taketoday.springdatawebsupport.application.test;

import cn.tuyucheng.taketoday.springdatawebsupport.application.controllers.UserController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

	@Autowired
	private UserController userController;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void whenUserControllerInjected_thenNotNull() throws Exception {
		assertThat(userController).isNotNull();
	}

	@Test
	void whenGetRequestToUsersEndPointWithIdPathVariable_thenCorrectResponse() throws Exception {
		mockMvc.perform(get("/users/{id}", "1").contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1"));
	}

	@Test
	void whenGetRequestToUsersEndPoint_thenCorrectResponse() throws Exception {
		mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$['pageable']['paged']").value("true"));
	}

	@Test
	void whenGetRequestToUserEndPointWithNameRequestParameter_thenCorrectResponse() throws Exception {
		mockMvc.perform(get("/users").param("name", "John").contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$['content'][0].['name']").value("John"));
	}

	@Test
	void whenGetRequestToSorteredUsersEndPoint_thenCorrectResponse() throws Exception {
		mockMvc.perform(get("/sortedusers").contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$['sort']['sorted']").value("true"));
	}

	@Test
	void whenGetRequestToFilteredUsersEndPoint_thenCorrectResponse() throws Exception {
		mockMvc.perform(get("/filteredusers").param("name", "John").contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("John"));
	}
}