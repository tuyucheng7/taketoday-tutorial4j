package cn.tuyucheng.taketoday.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WireMockServerUnitTest {

	static WireMockServer wireMockServer = new WireMockServer();

	@BeforeAll
	public static void beforeAll() {
		// WireMock.configureFor("custom-host", 9000, "/api-root-url");
		wireMockServer.start();
	}

	@AfterAll
	public static void afterAll() {
		wireMockServer.stop();
	}

	@AfterEach
	public void afterEach() {
		wireMockServer.resetAll();
	}

	@Test
	public void contextLoads() {
		assertTrue(true);
	}

	@Test
	public void simplestStubTest() {
		String api_url = "/resource";
		String responseBody = "Hello world!";

		stubFor(get(urlEqualTo("/resource"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader(HttpHeaders.CONTENT_TYPE,
					MediaType.APPLICATION_JSON_VALUE)
				.withBody("Hello world!")));


		// Given
		stubFor(get(api_url)
			.willReturn(ok(responseBody)));

		// Test
		ResponseEntity<String> response = getForEntity(api_url);

		// Verify
		assertEquals(response.getBody(), responseBody);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void authStubTest() {
		String api_url = "/protected-url";

		stubFor(get(api_url)
			.willReturn(unauthorized()));
		ResponseEntity<String> response = getForEntity(api_url);

		assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void postApiStubTest() {
		String api_url = "/create-resource";
		String requestBody = "{\"data\":\"empty\"}";
		String responseBody = "{\"data\":\"payload\"}";

		stubFor(post(api_url)
			.withRequestBody(equalToJson(requestBody))
			.willReturn(aResponse()
				.withStatus(201)
				.withHeader(HttpHeaders.CONTENT_TYPE,
					MediaType.APPLICATION_JSON_VALUE)
				.withBody(responseBody)));

		ResponseEntity<String> response = postForEntity(api_url, requestBody);

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		assertEquals(response.getBody(), responseBody);

		verify(exactly(1), postRequestedFor(urlEqualTo(api_url)));

		// verify(lessThan(5), anyRequestedFor(anyUrl()));
		// verify(lessThanOrExactly(5), anyRequestedFor(anyUrl()));
		// verify(exactly(5), anyRequestedFor(anyUrl()));
		// verify(moreThanOrExactly(5), anyRequestedFor(anyUrl()));
		// verify(moreThan(5), anyRequestedFor(anyUrl()));
	}

	@Test
	public void stubWithJson() {
		stubFor(get(urlEqualTo("/json-resource"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader(HttpHeaders.CONTENT_TYPE,
					MediaType.APPLICATION_JSON_VALUE)
				.withBody("{ \"message\": \"Hello world!\" }")));

		ResponseEntity<String> response = getForEntity("/json-resource");

		assertEquals(response.getBody(), "{ \"message\": \"Hello world!\" }");
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0),
			MediaType.APPLICATION_JSON_VALUE);
	}

	private ResponseEntity<String> getForEntity(String url) {
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		return testRestTemplate.
			getForEntity(wireMockServer.baseUrl() + url, String.class);
	}

	private ResponseEntity<String> postForEntity(String url, String requestBody) {
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		return testRestTemplate.
			postForEntity(wireMockServer.baseUrl() + url, requestBody, String.class);
	}
}