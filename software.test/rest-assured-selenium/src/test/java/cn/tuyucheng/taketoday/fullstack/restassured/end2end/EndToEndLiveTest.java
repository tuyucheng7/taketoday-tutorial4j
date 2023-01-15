package cn.tuyucheng.taketoday.fullstack.restassured.end2end;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EndToEndLiveTest {

	@Test
	void whenRegistration_thenShouldSuccessful() {
		baseURI = "https://bookstore.toolsqa.com";

		JSONObject requestParams = new JSONObject();
		requestParams.put("userName", "TakeToday-Test");
		requestParams.put("password", "Test@@123");

		Response response = given()
			.contentType(ContentType.JSON)
			.body(requestParams.toJSONString())
			.post("/Account/v1/User");

		assertEquals(201, response.getStatusCode());

		// We will need the userID in the response body for out tests, please save it in a local variable.
		// userID = d731254a-070b-40c2-a17d-8e4911d57b20
		String userID = response.getBody().jsonPath().getString("userID");
		System.out.println(userID);
	}

	@Test
	void completionBookSystem_end2endRestApiTest() {
		String userID = "d731254a-070b-40c2-a17d-8e4911d57b20";
		String userName = "TakeToday-Test";
		String password = "Test@@123";
		baseURI = "https://bookstore.toolsqa.com";

		String requestBody = "{ \"userName\":\"" + userName + "\", \"password\":\"" + password + "\"}";

		RequestSpecification request = given();

		// Step 1：Test will start from generating Token for Authorization.
		request.header("Content-Type", "application/json");
		Response response = request
			.body(requestBody)
			.post("/Account/v1/GenerateToken");

		String jsonString = response.asString();
		assertTrue(jsonString.contains("token"));
		assertEquals(200, response.getStatusCode());

		// This token will be used in later requests.
		String token = JsonPath.from(jsonString).get("token");

		// Step 2：Get Books - No auth is required for this.
		response = request.get("/BookStore/v1/Books");

		assertEquals(200, response.getStatusCode());
		jsonString = response.asString();
		List<Map<String, String>> books = JsonPath.from(jsonString).get("books");

		assertTrue(books.size() > 0);

		// This bookId will be used in later requests, to add the book with respective isbn.
		String bookId = books.get(0).get("isbn");

		// Step 3：Add a book - with auth, The token we had saved in the variable before from response in Stop 1,
		// we will be passing in the headers for each of the succeeding request.
		response = request.header("Authorization", "Bearer " + token)
			.header("Content-Type", "application/json")
			.body("{ \"userId\": \"" + userID + "\", " + "\"collectionOfIsbns\": [ { \"isbn\": \"" + bookId + "\" } ]}")
			.post("/BookStore/v1/Books");
		assertEquals(201, response.getStatusCode());

		// Step 4：Delete a book - with Auth.
		response = given().header("Authorization", "Bearer " + token)
			.header("Content-Type", "application/json")
			.body("{ \"isbn\": \"" + bookId + "\", \"userId\": \"" + userID + "\"}")
			.delete("BookStore/v1/Book");
		assertEquals(204, response.getStatusCode());

		// Step 5： Get User again.
		response = given().header("Authorization", "Bearer " + token)
			.header("Content-Type", "application/json")
			.get("/Account/v1/User/" + userID);
		assertEquals(200, response.getStatusCode());

		List<Map<String, String>> booksOfUser = JsonPath.from(response.asString()).get("books");
		assertEquals(0, booksOfUser.size());
	}
}