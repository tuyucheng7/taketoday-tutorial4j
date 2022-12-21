package cn.tuyucheng.taketoday.boot.client;

import cn.tuyucheng.taketoday.boot.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(SpringExtension.class)
@RestClientTest({DetailsServiceClient.class, Application.class})
class DetailsServiceClientIntegrationTest {

	@Autowired
	private DetailsServiceClient client;

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() throws Exception {
		String detailsString = objectMapper.writeValueAsString(new Details("John Smith", "john"));
		this.server.expect(requestTo("/john/details"))
			.andRespond(withSuccess(detailsString, MediaType.APPLICATION_JSON));
	}

	@Test
	void whenCallingGetUserDetails_thenClientExecutesCorrectCall() throws Exception {
		Details details = this.client.getUserDetails("john");

		assertThat(details.getLogin()).isEqualTo("john");
		assertThat(details.getName()).isEqualTo("John Smith");
	}
}