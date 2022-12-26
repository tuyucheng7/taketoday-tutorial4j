package cn.tuyucheng.taketoday.maxhttpheadersize.controller;

import cn.tuyucheng.taketoday.maxhttpheadersize.config.MaxHTTPHeaderSizeConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MaxHTTPHeaderSizeConfig.class)
@WebAppConfiguration
class MaxHttpHeaderSizeControllerIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.build();
	}

	@Test
	void givenTokenWithLessThan8KBLength_whenSendGetRequest_thenReturnsOK() throws Exception {
		mockMvc.perform(get("/request-header-test").contentType(MediaType.APPLICATION_JSON_VALUE)
				.with(httpBasic("user", "password"))
				.header("token", "token"))
			.andExpect(status().isOk());
	}

	@Test
	void givenTokenIsMissingInHeader_whenSendGetRequest_thenThrowsBadRequest() throws Exception {
		mockMvc.perform(get("/request-header-test")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.with(httpBasic("user", "password")))
			.andExpect(status().isBadRequest());
	}
}