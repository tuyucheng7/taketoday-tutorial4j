package cn.tuyucheng.taketoday.heap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notANumber;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MemoryStatusController.class)
class MemoryStatusControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Test
	void whenGetMemoryStatistics_thenReturnJsonArray() throws Exception {
		mvc.perform(get("/memory-status").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("heapSize", not(notANumber())))
			.andExpect(jsonPath("heapMaxSize", not(notANumber())))
			.andExpect(jsonPath("heapFreeSize", not(notANumber())));
	}
}