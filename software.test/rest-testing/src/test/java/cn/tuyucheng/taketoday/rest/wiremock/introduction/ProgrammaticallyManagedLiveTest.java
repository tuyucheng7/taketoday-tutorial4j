package cn.tuyucheng.taketoday.rest.wiremock.introduction;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;

public class ProgrammaticallyManagedLiveTest {

	private static final String TUYUCHENG_PATH = "/tuyucheng";

	private WireMockServer wireMockServer = new WireMockServer();
	private CloseableHttpClient httpClient = HttpClients.createDefault();

	@Test
	public void givenProgrammaticallyManagedServer_whenUsingSimpleStubbing_thenCorrect() throws IOException {
		wireMockServer.start();

		configureFor("localhost", 8080);
		stubFor(get(urlEqualTo(TUYUCHENG_PATH)).willReturn(aResponse().withBody("Welcome to Tuyucheng!")));

		HttpGet request = new HttpGet("http://localhost:8080/tuyucheng");
		HttpResponse httpResponse = httpClient.execute(request);
		String stringResponse = convertResponseToString(httpResponse);

		verify(getRequestedFor(urlEqualTo(TUYUCHENG_PATH)));
		assertEquals("Welcome to Tuyucheng!", stringResponse);

		wireMockServer.stop();
	}

	private static String convertResponseToString(HttpResponse response) throws IOException {
		InputStream responseStream = response.getEntity().getContent();
		Scanner scanner = new Scanner(responseStream, "UTF-8");
		String stringResponse = scanner.useDelimiter("\\Z").next();
		scanner.close();
		return stringResponse;
	}
}