package cn.tuyucheng.taketoday.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class MyFunctionsUnitTest {

	@Test
	public void givenFunctionAPI_whenCallWithoutBody_thenShouldReturnDefault() {
		given()
			.post("/GreetUser")
			.then()
			.statusCode(200)
			.body(containsString("Hello Funqy!"));
	}

	@Test
	public void givenFunctionAPI_whenCallWithName_thenShouldReturnName() {
		given()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Friend\"}")
			.post("/GreetUser")
			.then()
			.statusCode(200)
			.body(containsString("Hello Friend!"));
	}

	@Test
	public void givenFunctionAPI_whenCallWithEvent_thenShouldReturn200() {
		RestAssured.given().contentType("application/json")
			.header("ce-specversion", "1.0")
			.header("ce-id", UUID.randomUUID().toString())
			.header("ce-type", "GreetUser")
			.header("ce-source", "test")
			.body("{ \"name\": \"Tuyucheng\" }")
			.post("/")
			.then().statusCode(200);
	}
}