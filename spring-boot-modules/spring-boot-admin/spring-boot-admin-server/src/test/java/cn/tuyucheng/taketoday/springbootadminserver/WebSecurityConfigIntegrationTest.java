package cn.tuyucheng.taketoday.springbootadminserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSecurityConfigIntegrationTest {

	@Autowired
	WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(wac)
			.build();
	}

	@Test
	void whenApplicationStarts_ThenGetLoginPageWithSuccess() throws Exception {
		mockMvc.perform(get("/login.html"))
			.andExpect(status().is2xxSuccessful());
	}

	@Test
	void whenFormLoginAttempted_ThenSuccess() throws Exception {
		mockMvc.perform(formLogin("/login")
			.user("admin")
			.password("admin"));
	}

	@Test
	void whenFormLoginWithSuccess_ThenApiEndpointsAreAccessible() throws Exception {
		mockMvc.perform(formLogin("/login")
			.user("admin")
			.password("admin"));

		mockMvc.perform(get("/applications/"))
			.andExpect(status().is2xxSuccessful());
	}

	@Test
	void whenHttpBasicAttempted_ThenSuccess() throws Exception {
		mockMvc.perform(get("/actuator/env").with(httpBasic("admin", "admin")));
	}

	@Test
	void whenInvalidHttpBasicAttempted_ThenUnauthorized() throws Exception {
		mockMvc.perform(get("/actuator/env").with(httpBasic("admin", "invalid")))
			.andExpect(unauthenticated());
	}
}