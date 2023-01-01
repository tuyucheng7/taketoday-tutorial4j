package cn.tuyucheng.taketoday.students;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {

	private static final String STUDENTS_PATH = "/students/";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void whenReadAll_thenStatusIsOk() throws Exception {
		this.mockMvc.perform(get(STUDENTS_PATH))
			.andExpect(status().isOk());
	}

	@Test
	void whenReadOne_thenStatusIsOk() throws Exception {
		this.mockMvc.perform(get(STUDENTS_PATH + 1))
			.andExpect(status().isOk());
	}

	@Test
	void whenCreate_thenStatusIsCreated() throws Exception {
		Student student = new Student(10, "Albert", "Einstein");
		this.mockMvc.perform(post(STUDENTS_PATH).content(asJsonString(student))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isCreated());
	}

	@Test
	void whenUpdate_thenStatusIsOk() throws Exception {
		Student student = new Student(1, "Nikola", "Tesla");
		this.mockMvc.perform(put(STUDENTS_PATH + 1)
				.content(asJsonString(student))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk());
	}

	@Test
	void whenDelete_thenStatusIsNoContent() throws Exception {
		this.mockMvc.perform(delete(STUDENTS_PATH + 3))
			.andExpect(status().isNoContent());
	}

	private String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}