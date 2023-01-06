package cn.tuyucheng.taketoday.springboothsqldb.application.tests;

import cn.tuyucheng.taketoday.springboothsqldb.application.Application;
import cn.tuyucheng.taketoday.springboothsqldb.application.entities.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class CustomerControllerIntegrationTest {

	private static MediaType MEDIA_TYPE_JSON;

	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setUpJsonMediaType() {
		MEDIA_TYPE_JSON = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
	}

	@Test
	public void whenPostHttpRequestToCustomers_thenStatusOK() throws Exception {
		Customer customer = new Customer("John", "john@domain.com");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = objectWriter.writeValueAsString(customer);

		this.mockMvc.perform(post("/customers")
			.contentType(MEDIA_TYPE_JSON)
			.content(requestJson)
		).andExpect(status().isOk());
	}

	@Test
	public void whenGetHttpRequestToCustomers_thenStatusOK() throws Exception {
		this.mockMvc.perform(get("/customers"))
			.andExpect(content().contentType(MEDIA_TYPE_JSON))
			.andExpect(status().isOk());
	}
}