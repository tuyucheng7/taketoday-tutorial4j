package cn.tuyucheng.taketoday.spring.serverconfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest
class GreetingControllerIntegrationTest {

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private GreetingService greetingService;

	private final String name = "Tuyucheng";

	@BeforeEach
	public void setUp() {
		when(greetingService.greet(name)).thenReturn(Mono.just("Greeting Tuyucheng"));
	}

	@Test
	void shouldGreet() {
		webClient.get().uri("/greet/{name}", name)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(String.class)
			.isEqualTo("Greeting Tuyucheng");
	}
}