package cn.tuyucheng.taketoday.mime;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FooController.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ComponentScan({"cn.tuyucheng.taketoday.mime"})
@EnableAutoConfiguration
@ActiveProfiles("test")
class FooLiveTest {

	@LocalServerPort
	private int port;

	@Autowired
	protected IMarshaller marshaller;

	final void create() {
		create(new Foo(randomAlphabetic(6)));
	}

	final String createAsUri() {
		return createAsUri(new Foo(randomAlphabetic(6)));
	}

	protected final void create(final Foo resource) {
		createAsUri(resource);
	}

	private String createAsUri(final Foo resource) {
		final Response response = createAsResponse(resource);
		return getURL() + "/" + response.getBody().as(Foo.class).getId();
	}

	private Response createAsResponse(final Foo resource) {
		final String resourceAsString = marshaller.encode(resource);
		return RestAssured.given()
			.contentType(marshaller.getMime())
			.body(resourceAsString)
			.post(getURL());
	}

	protected String getURL() {
		return "http://localhost:" + port + "/foos";
	}

	@Test
	void givenResourceExists_whenRetrievingResource_thenEtagIsAlsoReturned() {
		// Given
		final String uriOfResource = createAsUri();

		// When
		final Response findOneResponse = RestAssured.given().header("Accept", "application/json").get(uriOfResource);

		// Then
		assertEquals(200, findOneResponse.getStatusCode());
	}
}