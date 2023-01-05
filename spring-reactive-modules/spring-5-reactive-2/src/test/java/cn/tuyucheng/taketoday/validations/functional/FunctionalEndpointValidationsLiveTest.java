package cn.tuyucheng.taketoday.validations.functional;

import cn.tuyucheng.taketoday.validations.functional.model.AnnotatedRequestEntity;
import cn.tuyucheng.taketoday.validations.functional.model.CustomRequestEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FunctionalEndpointValidationsLiveTest {

	private static final String BASE_URL = "http://localhost:8080";
	private static final String COMPLEX_EP_URL = BASE_URL + "/complex-handler-functional-validation";
	private static final String DRY_EP_URL = BASE_URL + "/dry-functional-validation";
	private static final String ANNOTATIONS_EP_URL = BASE_URL + "/annotated-functional-validation";

	private static WebTestClient client;

	@BeforeAll
	static void setup() {
		client = WebTestClient.bindToServer()
			.baseUrl(BASE_URL)
			.build();
	}

	@Test
	void whenRequestingDryEPWithInvalidBody_thenObtainBadRequest() {
		CustomRequestEntity body = new CustomRequestEntity("name", "123");

		ResponseSpec response = client.post()
			.uri(DRY_EP_URL)
			.body(Mono.just(body), CustomRequestEntity.class)
			.exchange();

		response.expectStatus()
			.isBadRequest();
	}

	@Test
	void whenRequestingComplexEPWithInvalidBody_thenObtainBadRequest() {
		CustomRequestEntity body = new CustomRequestEntity("name", "123");

		ResponseSpec response = client.post()
			.uri(COMPLEX_EP_URL)
			.body(Mono.just(body), CustomRequestEntity.class)
			.exchange();

		response.expectStatus()
			.isBadRequest();
	}

	@Test
	void whenRequestingAnnotatedEPWithInvalidBody_thenObtainBadRequest() {
		AnnotatedRequestEntity body = new AnnotatedRequestEntity("user", "passwordlongerthan7digits");

		ResponseSpec response = client.post()
			.uri(ANNOTATIONS_EP_URL)
			.body(Mono.just(body), AnnotatedRequestEntity.class)
			.exchange();

		response.expectStatus()
			.isBadRequest();
	}

	@Test
	void whenRequestingDryEPWithValidBody_thenObtainBadRequest() {
		CustomRequestEntity body = new CustomRequestEntity("name", "1234567");

		ResponseSpec response = client.post()
			.uri(DRY_EP_URL)
			.body(Mono.just(body), CustomRequestEntity.class)
			.exchange();

		response.expectStatus()
			.isOk();
	}

	@Test
	void whenRequestingComplexEPWithValidBody_thenObtainBadRequest() {
		CustomRequestEntity body = new CustomRequestEntity("name", "1234567");

		ResponseSpec response = client.post()
			.uri(COMPLEX_EP_URL)
			.body(Mono.just(body), CustomRequestEntity.class)
			.exchange();

		response.expectStatus()
			.isOk();
	}

	@Test
	void whenRequestingAnnotatedEPWithValidBody_thenObtainBadRequest() {
		AnnotatedRequestEntity body = new AnnotatedRequestEntity("user", "12345");

		ResponseSpec response = client.post()
			.uri(ANNOTATIONS_EP_URL)
			.body(Mono.just(body), AnnotatedRequestEntity.class)
			.exchange();

		response.expectStatus()
			.isOk();
	}
}