package cn.tuyucheng.taketoday.reactive.filters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser
class UserControllerIntegrationTest {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void whenUserNameIsTuyucheng_thenWebFilterIsApplied() {
		EntityExchangeResult<String> result = webTestClient.get().uri("/users/tuyucheng")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.returnResult();

		assertEquals(result.getResponseBody(), "tuyucheng");
		assertEquals(result.getResponseHeaders().getFirst("web-filter"), "web-filter-test");
	}

	@Test
	void whenUserNameIsTest_thenHandlerFilterFunctionIsNotApplied() {
		webTestClient.get().uri("/users/test")
			.exchange()
			.expectStatus().isOk();
	}
}