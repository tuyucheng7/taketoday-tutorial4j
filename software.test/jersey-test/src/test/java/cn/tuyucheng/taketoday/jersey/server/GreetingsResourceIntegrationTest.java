package cn.tuyucheng.taketoday.jersey.server;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreetingsResourceIntegrationTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(Greetings.class);
	}

	@Test
	public void givenGetHiGreeting_whenCorrectRequest_thenResponseIsOkAndContainsHi() {
		Response response = target("/greetings/hi").request()
			.get();

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), "Http Response should be 200: ");
		assertEquals(MediaType.TEXT_HTML, response.getHeaderString(HttpHeaders.CONTENT_TYPE), "Http Content-Type should be: ");

		String content = response.readEntity(String.class);
		assertEquals("hi", content, "Content of response is: ");
	}
}