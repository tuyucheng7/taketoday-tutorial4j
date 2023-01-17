package cn.tuyucheng.taketoday;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DemoApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationIntegrationTest {

	private RestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp() {
		restTemplate = new RestTemplate();
	}

	@Test
	void contextLoads() {
		final String result = restTemplate.getForObject("http://localhost:" + port + "/", String.class);
		assertThat(result).isEqualTo("Hello world");
	}
}