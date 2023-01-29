package cn.tuyucheng.taketoday.spring.amqp;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

class SpringWebfluxAmqpLiveTest {

	@Test
	void whenSendingAMessageToQueue_thenAcceptedReturnCode() {

		WebTestClient client = WebTestClient.bindToServer()
			.baseUrl("http://localhost:8080")
			.build();

		client.post()
			.uri("/queue/NYSE")
			.syncBody("Test Message")
			.exchange()
			.expectStatus().isAccepted();
	}
}