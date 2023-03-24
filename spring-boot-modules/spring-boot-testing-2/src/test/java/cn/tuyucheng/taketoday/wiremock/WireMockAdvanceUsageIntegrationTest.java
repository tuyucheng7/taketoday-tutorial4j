package cn.tuyucheng.taketoday.wiremock;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingXPath;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@WireMockTest
public class WireMockAdvanceUsageIntegrationTest {

	@Test
	void requestMatching() {
		stubFor(get(urlPathEqualTo("/resource-url"))
			.withHeader("Accept", containing("xml"))
			.withCookie("JSESSIONID", matching(".*"))
			.withQueryParam("param-name", equalTo("param-value"))
			.withBasicAuth("username", "plain-password")
			// .withRequestBody(equalToXml("part-of-request-body"))
			.withRequestBody(matchingXPath("//root-tag"))
			// .withMultipartRequestBody(
			// 	aMultipart()
			// 		.withName("preview-image")
			// 		.withHeader("Content-Type", containing("image"))
			// 		.withBody(equalToJson("{}"))
			// )
			.willReturn(aResponse()));

		Assertions.assertTrue(true);
	}

	@Test
	void delayedResponse() {
		UUID stubId = UUID.randomUUID();

		stubFor(get(urlEqualTo("/delayed"))
			.withId(stubId)
			.willReturn(
				aResponse()
					.withFixedDelay(2000)
					.withFault(Fault.MALFORMED_RESPONSE_CHUNK)
				// .withLogNormalRandomDelay(90, 0.1)
				// .withRandomDelay(new UniformDistribution(15, 25))
			));
		// Test delayed response

		stubFor(any(anyUrl())
			.withId(stubId)
			.willReturn(aResponse()
				.withFixedDelay(10)));
		// Test in-time response
	}

	@Test
	void editStub() {
		UUID stubId = UUID.randomUUID();

		stubFor(get(urlEqualTo("/edit-this"))
			.withId(stubId)
			.willReturn(aResponse()
				.withBody("Original Response")));
		// Later when want to modify the stub

		stubFor(any(anyUrl())
			.withId(stubId)
			.willReturn(aResponse()
				.withBody("Modified Response")));
	}

	private String getContent(String url) {
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		return testRestTemplate.getForObject(url, String.class);
	}
}