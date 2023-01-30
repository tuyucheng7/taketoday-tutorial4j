package cn.tuyucheng.taketoday.spring.responsestatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResponseStatusControllerLiveTest {

	@Autowired
	private WebTestClient testClient;

	@Test
	void whenCallRest_thenStatusIsOk() {
		testClient.get()
			.uri("/statuses/ok")
			.exchange()
			.expectStatus()
			.isOk();
	}

	@Test
	void whenCallRest_thenStatusIsNoContent() {
		testClient.get()
			.uri("/statuses/no-content")
			.exchange()
			.expectStatus()
			.isNoContent();
	}

	@Test
	void whenCallRest_thenStatusIsAccepted() {
		testClient.get()
			.uri("/statuses/accepted")
			.exchange()
			.expectStatus()
			.isAccepted();
	}

	@Test
	void whenCallRest_thenStatusIsBadRequest() {
		testClient.get()
			.uri("/statuses/bad-request")
			.exchange()
			.expectStatus()
			.isBadRequest();
	}

	@Test
	void whenCallRest_thenStatusIsUnauthorized() {
		testClient.get()
			.uri("/statuses/unauthorized")
			.exchange()
			.expectStatus()
			.isUnauthorized();
	}

	@Test
	void whenCallRest_thenStatusIsNotFound() {
		testClient.get()
			.uri("/statuses/not-found")
			.exchange()
			.expectStatus()
			.isNotFound();
	}
}