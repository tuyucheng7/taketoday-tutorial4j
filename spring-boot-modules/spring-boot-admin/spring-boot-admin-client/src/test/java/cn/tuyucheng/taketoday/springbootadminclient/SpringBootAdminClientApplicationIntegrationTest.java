package cn.tuyucheng.taketoday.springbootadminclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class SpringBootAdminClientApplicationIntegrationTest {

	@Autowired
	Environment environment;

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
	void whenEnvironmentAvailable_ThenAdminServerPropertiesExist() {
		assertEquals(environment.getProperty("spring.boot.admin.client.url"), "http://localhost:8080");
		assertEquals(environment.getProperty("spring.boot.admin.client.username"), "admin");
		assertEquals(environment.getProperty("spring.boot.admin.client.password"), "admin");
	}

	@Test
	void whenHttpBasicAttempted_ThenSuccess() throws Exception {
		mockMvc.perform(get("/actuator/env").with(httpBasic("client", "client")));
	}

	@Test
	void whenInvalidHttpBasicAttempted_ThenUnauthorized() throws Exception {
		mockMvc.perform(get("/actuator/env").with(httpBasic("client", "invalid")))
			.andExpect(unauthenticated());
	}
}