package cn.tuyucheng.taketoday.fullstack.restassured.authentication;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RestAssuredWithAuthenticationApplicationLiveTest {

	@BeforeEach
	void setUp() {
		baseURI = "http://localhost:8080/instructors/courses";
	}

	@Test
	void givenUnauthorizedUser_whenAccessSecretUrl_thenShouldReturn401() {
		Response response = given().get();

		assertEquals(401, response.getStatusCode());
		assertEquals("Unauthorized", response.jsonPath().getString("error"));
	}

	@Test
	void givenValidCredential_whenAccessSecretUrl_thenShouldReturn200() {
		Response response = given()
			.auth()
			.basic("tuyucheng", "123456")
			.get();

		JsonPath jsonPath = response.jsonPath();

		assertEquals(200, response.getStatusCode());
	}

	@Test
	void whenAccessPostmanEchoApi_andUnauthorized_thenShouldFailed() {
		String responseBody = given()
			.get("https://postman-echo.com/basic-auth")
			.body()
			.asString();

		assertEquals("Unauthorized", responseBody);
	}

	@Test
	void whenAccessPostmanEchoApi_andAuthorized_thenShouldSuccess() {
		ResponseBody responseBody = given()
			.auth()
			.basic("postman", "password")
			.get("https://postman-echo.com/basic-auth")
			.body();

		assertEquals("true", responseBody.jsonPath().getString("authenticated"));
	}

	@Test
	void whenAccessPostmanEchoApi_andUsingPreemptiveAuthentication_thenShouldSuccess() {
		ResponseBody responseBody = given()
			.auth()
			.preemptive()
			.basic("postman", "password")
			.get("https://postman-echo.com/basic-auth")
			.body();

		assertEquals("true", responseBody.jsonPath().getString("authenticated"));
	}

	@Test
	void whenAccessPostmanEchoApi_andUsingDigestAuthentication_thenShouldSuccess() {
		ResponseBody responseBody = given()
			.auth()
			.digest("postman", "password")
			.get("https://postman-echo.com/basic-auth")
			.body();

		assertEquals("true", responseBody.jsonPath().getString("authenticated"));
	}
}