package cn.tuyucheng.taketoday.wiremock;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest
public class WireMockTestAnnotationIntegrationTest {

	@Test
	void simpleStubTesting(WireMockRuntimeInfo wmRuntimeInfo) {
		String responseBody = "Hello World !!";
		String apiUrl = "/resource";

		stubFor(get("/resource").willReturn(ok(responseBody)));

		String apiResponse = getContent(wmRuntimeInfo.getHttpBaseUrl() + apiUrl);
		assertEquals(apiResponse, responseBody);

		verify(getRequestedFor(urlEqualTo(apiUrl)));
	}

	private String getContent(String url) {
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		return testRestTemplate.getForObject(url, String.class);
	}
}