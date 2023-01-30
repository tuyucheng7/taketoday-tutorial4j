package cn.tuyucheng.taketoday.spring.serverconfig;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import javax.net.ssl.SSLException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
class TimeoutLiveTest {

	private static final String BASE_URL = "https://localhost:8443";
	private static final int TIMEOUT_MILLIS = 2000;

	private WebTestClient webTestClient;

	@BeforeEach
	void setup() throws SSLException {
		webTestClient = WebTestClient.bindToServer(getConnector())
			.baseUrl(BASE_URL)
			.build();
	}

	@Test
	void shouldTimeout() {
		// exception.expect(ReadTimeoutException.class);
		assertThrows(ReadTimeoutException.class, () -> webTestClient.get()
			.uri("/timeout/{timeout}", 3)
			.exchange());
	}

	@Test
	void shouldNotTimeout() {
		WebTestClient.ResponseSpec response = webTestClient.get()
			.uri("/timeout/{timeout}", 1)
			.exchange();
		response.expectStatus()
			.isOk()
			.expectBody(String.class)
			.isEqualTo("OK");
	}

	private ReactorClientHttpConnector getConnector() throws SSLException {
		SslContext sslContext = SslContextBuilder
			.forClient()
			.trustManager(InsecureTrustManagerFactory.INSTANCE)
			.build();

		TcpClient tcpClient = TcpClient
			.create()
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_MILLIS)
			.doOnConnected(connection -> {
				connection.addHandlerLast(new ReadTimeoutHandler(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
				connection.addHandlerLast(new WriteTimeoutHandler(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
			});

		HttpClient httpClient = HttpClient.from(tcpClient).secure(t -> t.sslContext(sslContext));
		return new ReactorClientHttpConnector(httpClient);
	}
}