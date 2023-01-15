package cn.tuyucheng.taketoday.fullstack.restassured;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryParamLiveTest {

	@Test
	void givenQueryParam_whenAccessBook_thenResponseShouldContainsExactRecord() {
		baseURI = "https://demoqa.com/BookStore/v1";

		RequestSpecification httpRequest = given();
		// Passing the resource details
		Response response = httpRequest.queryParam("ISBN", "9781449325862").get("/Book");
		String asStringBody = response.body().asString();

		// Creating object of JsonPath and passing the string response body as parameter.
		JsonPath jsonPath = new JsonPath(asStringBody);
		// Storing publisher name in a string variable.
		String title = jsonPath.getString("title");

		assertEquals("Git Pocket Guide", title);
	}

	@Test
	void givenJsonReqBody_whenPostBook_thenShouldCorrect() {
		baseURI = "https://demoqa.com/BookStore/v1";
		RequestSpecification request = given();

		// JSONObject is a class that represents a simple JSON. we can add key-value pairs using the put method.
		JSONObject requestParams = new JSONObject();
		requestParams.put("userId", "TQ123");
		requestParams.put("isbn", "9781449325862");

		// convert JSONObject to the string representation.
		String requestBody = requestParams.toJSONString();

		// Add a header stating the Request body is a JSON.
		request.header("Content-Type", "application/json");
		// Post the request and che the response.
		request.body(requestBody);

		Response response = request.post("/Books");

		assertEquals(200, response.getStatusCode());
	}
}