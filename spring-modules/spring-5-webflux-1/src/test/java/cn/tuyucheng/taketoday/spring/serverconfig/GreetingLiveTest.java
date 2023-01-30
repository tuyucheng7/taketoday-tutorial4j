package cn.tuyucheng.taketoday.spring.serverconfig;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
class GreetingLiveTest {

	private static final String BASE_URL = "https://localhost:8443";

	private WebTestClient webTestClient;

	@BeforeEach
	void setup() throws SSLException {
		webTestClient = WebTestClient.bindToServer(getConnector())
			.baseUrl(BASE_URL)
			.build();
	}

	@Test
	void shouldGreet() {
		final String name = "Tuyucheng";

		ResponseSpec response = webTestClient.get()
			.uri("/greet/{name}", name)
			.exchange();

		response.expectStatus()
			.isOk()
			.expectBody(String.class)
			.isEqualTo("Greeting Tuyucheng");
	}

	private ReactorClientHttpConnector getConnector() throws SSLException {
		SslContext sslContext = SslContextBuilder
			.forClient()
			.trustManager(InsecureTrustManagerFactory.INSTANCE)
			.build();
		HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
		return new ReactorClientHttpConnector(httpClient);
	}
}