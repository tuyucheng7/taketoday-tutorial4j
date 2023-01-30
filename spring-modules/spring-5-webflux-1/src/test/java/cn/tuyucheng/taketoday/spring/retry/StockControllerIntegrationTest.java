package cn.tuyucheng.taketoday.spring.retry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@WebFluxTest
class StockControllerIntegrationTest {

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private ExternalConnector externalConnector;

	@Test
	void shouldReturnStockData() {
		given(externalConnector.getData("ABC")).willReturn(Mono.just("stock data"));

		webClient.get()
			.uri("/stocks/data/{id}", "ABC")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(String.class)
			.isEqualTo("stock data");
	}
}