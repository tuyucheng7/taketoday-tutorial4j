package cn.tuyucheng.taketoday.fullstack.restassured;

import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RestAssuredSerializationLiveTest {

	@Test
	void givenANonExistsCourse_whenSaveIsCalled_thenShouldReturn200() {
		baseURI = "http://localhost:8080/instructors/courses";

		JSONObject requestParams = new JSONObject();

		requestParams.put("id", "5");
		requestParams.put("username", "tuyucheng");
		requestParams.put("description", "Learn Full stack with Spring Boot and Vuejs");

		JSONSuccessResponse responseBody = given()
			.contentType(ContentType.JSON)
			.body(requestParams.toJSONString())
			.post("/")
			.getBody()
			// Deserialize the Response body into JSONFailureResponse.
			.as(JSONSuccessResponse.class);

		assertEquals(200, responseBody.getStatusCode());
		assertEquals("saved successful", responseBody.getMessage());
	}

	@Test
	void givenAExistsCourse_whenSaveIsCalled_thenShouldReturn400() {
		baseURI = "http://localhost:8080/instructors/courses";

		JSONObject requestParams = new JSONObject();
		requestParams.put("id", "5");
		requestParams.put("username", "tuyucheng");
		requestParams.put("description", "Learn Full stack with Spring Boot and Vuejs");

		int statusCode = given()
			.contentType(ContentType.JSON)
			.body(requestParams.toJSONString())
			.post("/")
			.getStatusCode();

		assertEquals(400, statusCode, "the Course with id 5 already exists!");
	}
}