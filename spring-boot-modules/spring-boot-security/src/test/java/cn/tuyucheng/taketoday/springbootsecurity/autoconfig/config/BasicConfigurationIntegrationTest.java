package cn.tuyucheng.taketoday.springbootsecurity.autoconfig.config;

import cn.tuyucheng.taketoday.springbootsecurity.autoconfig.SpringBootSecurityApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringBootSecurityApplication.class)
class BasicConfigurationIntegrationTest {

	TestRestTemplate restTemplate;
	URL base;

	@LocalServerPort
	int port;

	@BeforeEach
	void setUp() throws MalformedURLException {
		restTemplate = new TestRestTemplate("user", "password");
		base = new URL("http://localhost:" + port);
	}

	@Test
	void whenLoggedUserRequestsHomePage_ThenSuccess() throws IllegalStateException, IOException {
		ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(Objects.requireNonNull(response
				.getBody())
			.contains("Tuyucheng"));
	}

	@Test
	void whenUserWithWrongCredentials_thenUnauthorizedPage() throws IllegalStateException, IOException {
		restTemplate = new TestRestTemplate("user", "wrongpassword");
		ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertNull(response.getBody());
	}
}