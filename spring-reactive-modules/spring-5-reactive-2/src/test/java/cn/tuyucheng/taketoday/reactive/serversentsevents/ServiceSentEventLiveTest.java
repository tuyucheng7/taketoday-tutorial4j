package cn.tuyucheng.taketoday.reactive.serversentsevents;

import cn.tuyucheng.taketoday.reactive.serversentevents.server.ServerSSEApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = ServerSSEApplication.class)
@WithMockUser
class ServiceSentEventLiveTest {

	private WebTestClient client = WebTestClient.bindToServer()
		.baseUrl("http://localhost:8081/sse-server")
		.build();

	@Test
	void whenSSEEndpointIsCalled_thenEventStreamingBegins() {
		Executable sseStreamingCall = () -> client.get()
			.uri("/stream-sse")
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
			.expectBody(String.class);

		Assertions.assertThrows(IllegalStateException.class, sseStreamingCall, "Expected test to timeout and throw IllegalStateException, but it didn't");
	}

	@Test
	void whenFluxEndpointIsCalled_thenEventStreamingBegins() {
		Executable sseStreamingCall = () -> client.get()
			.uri("/stream-flux")
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader()
			.contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
			.expectBody(String.class);

		Assertions.assertThrows(IllegalStateException.class, sseStreamingCall, "Expected test to timeout and throw IllegalStateException, but it didn't");
	}
}