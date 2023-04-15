package cn.tuyucheng.taketoday.boot.grpc.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LocalGrpcClientApplication.class)
@AutoConfigureMockMvc
public class GrpcClientControllerIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private GrpcClientService grpcClientService;

	@Test
	void givenQueryParam_whenGetRequest_thenCorrectResponse() throws Exception {
		when(grpcClientService.sendMessage("Tuyucheng")).thenReturn("Hello Tuyucheng");

		mockMvc.perform(get("/").param("name", "Tuyucheng"))
			.andExpect(status().isOk())
			.andExpect(content().string("Hello Tuyucheng"));
	}

	@Test
	void givenEmptyQueryParam_whenGetRequest_thenReturnDefaultResponse() throws Exception {
		when(grpcClientService.sendMessage("Michael")).thenReturn("Hello Michael");

		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(content().string("Hello Michael"));
	}
}