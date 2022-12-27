package cn.tuyucheng.taketoday.boot.autoconfig;

import cn.tuyucheng.taketoday.boot.Application;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class AutoConfigIntegrationTest {

	/**
	 * Encapsulates the random port the test server is listening on.
	 */
	@LocalServerPort
	private int port;

	@Test
	void givenNoAuthentication_whenAccessHome_thenUnauthorized() {
		int statusCode = RestAssured.get("http://localhost:" + port).statusCode();
		assertEquals(HttpStatus.UNAUTHORIZED.value(), statusCode);
	}

	@Test
	void givenAuthentication_whenAccessHome_thenOK() {
		int statusCode = RestAssured.given().auth().basic("john", "123").get("http://localhost:" + port).statusCode();
		assertEquals(HttpStatus.OK.value(), statusCode);
	}
}