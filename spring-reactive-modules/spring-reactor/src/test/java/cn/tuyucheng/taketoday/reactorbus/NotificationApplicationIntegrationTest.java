package cn.tuyucheng.taketoday.reactorbus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationApplicationIntegrationTest {

	@LocalServerPort
	private int port;

	@Test
	void givenAppStarted_whenNotificationTasksSubmitted_thenProcessed() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject("http://localhost:" + port + "/startNotification/10", String.class);
	}
}