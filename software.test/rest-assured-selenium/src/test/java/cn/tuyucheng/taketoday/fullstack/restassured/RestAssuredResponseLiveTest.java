package cn.tuyucheng.taketoday.fullstack.restassured;

import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestAssuredResponseLiveTest {

	@Test
	void whenAccessBooks_thenStatusCodeShouldBe200() {
		baseURI = "https://demoqa.com/BookStore/v1/Books";

		RequestSpecification httpRequest = given();
		Response response = httpRequest.get("");
		// Get the status code of the request. If request is successful, status code will be 200.
		int statusCode = response.getStatusCode();

		// Assert that correct status code is returned.
		assertEquals(200, statusCode, "InCorrect status code returned");
	}

	@Test
	void givenNoneExistsUserId_whenGetUserDetails_thenStatuesCodeShouldBe401() {
		baseURI = "https://demoqa.com/Account/v1/User";

		RequestSpecification httpRequest = given();
		Response response = httpRequest.get("test");
		int statusCode = response.getStatusCode();

		assertEquals(401, statusCode, "InCorrect status code returned");
	}

	@Test
	void givenAccessBooks_whenAssertStatusLine_thenShouldCorrect() {
		baseURI = "https://demoqa.com/BookStore/v1/Books";

		RequestSpecification httpRequest = given();
		Response response = httpRequest.get("");
		String statusLine = response.getStatusLine();

		// Get the status line from the Response in a variable called statusLine
		assertEquals("HTTP/1.1 200 OK", statusLine);
	}

	@Test
	void givenAccessBooks_whenGetAllResponseHeaders_thenIterating() {
		baseURI = "https://demoqa.com/BookStore/v1/Books";

		RequestSpecification httpRequest = given();
		Response response = httpRequest.get("");
		Headers allHeaders = response.headers();

		allHeaders.forEach(header -> System.out.println("Key: " + header.getName() + " Value: " + header.getValue()));
	}

	@Test
	void givenAccessBooks_whenGetSomeHeaders_thenAssertShouldBeCorrect() {
		baseURI = "https://demoqa.com/BookStore/v1/Books";

		RequestSpecification httpRequest = given();
		Response response = httpRequest.get("");

		String contentType = response.getHeader("Content-Type");
		assertEquals("application/json; charset=utf-8", contentType);

		String serverType = response.getHeader("Server");
		assertEquals("nginx/1.17.10 (Ubuntu)", serverType);

		String acceptLanguage = response.header("Content-Encoding");
		assertNull(acceptLanguage);
	}

	@Test
	void givenCityNameWithHyderabad_whenAccessWeather_thenPrintBody() {
		baseURI = "https://demoqa.com/utilities/weather/city";

		RequestSpecification httpRequest = given();
		Response response = httpRequest.get("/Hyderabad");

		// Retrieve the body of the Response
		ResponseBody body = response.getBody();

		// By using the ResponseBody.asString() method, we can convert the body into the string representation.
		System.out.println(body.asString());
	}

	@Test
	void givenCityNameWithHyderabad_whenAccessWeather_thenAssertBodyContainsHyderabad() {
		baseURI = "https://demoqa.com/utilities/weather/city";

		RequestSpecification httpRequest = given();
		Response response = httpRequest.get("/Hyderabad");

		ResponseBody body = response.getBody();
		String bodyAsString = body.asString();

		assertTrue(bodyAsString.contains("Hyderabad"));
	}

	@Test
	void givenCityNameWithHyderabad_whenAccessWeather_thenAssertCityNodeShouldCorrect() {
		baseURI = "https://demoqa.com/utilities/weather/city";

		Response response = given().get("/Hyderabad");

		// First get the JsonPath object instance from the Response interface.
		JsonPath jsonPathEvaluator = response.jsonPath();

		// Then simply query the JsonPath object to get a String value of the node specified by JsonPath: City
		// Note: You should not put $. in the Java node.
		String city = jsonPathEvaluator.get("City");

		assertEquals("Hyderabad", city);
	}
}