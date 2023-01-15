package cn.tuyucheng.taketoday.fullstack.restassured;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

class RestAssuredAPILiveTest {

	@Test
	void getBooksDetails() {
		// Specify the base URL to the Restful Web Service.
		baseURI = "https://demoqa.com/BookStore/v1/Books";
		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = given();
		// Specify the method type(GET) and the parameters if any. In this case the request does not take any parameters.
		Response response = httpRequest.request(Method.GET, "");
		// print the status and message body of the response received from the server.
		System.out.println("Status received => " + response.getStatusLine());
		System.out.println("Response => " + response.prettyPrint());
	}

	@Test
	void getWeatherDetailsCondensed() {
		baseURI = "https://demoqa.com/BookStore/v1/Books";
		RequestSpecification request = given();
		Response response = request.get("");
		System.out.println("Response Body is => " + response.asString());
	}
}