package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api;

import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.requests.AddBooksRequest;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.requests.AuthorizationRequest;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.requests.RemoveBookRequest;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.responses.Books;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.responses.Token;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.responses.UserAccount;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class Endpoints {

	private final RequestSpecification request;

	public Endpoints(String baseUrl) {
		baseURI = baseUrl;
		request = given().header("Content-Type", "application/json");
	}

	public void authenticateUser(AuthorizationRequest authorizationRequest) {
		Response response = request
			.body(authorizationRequest)
			.post(Route.generateToken());
		if (response.getStatusCode() != HttpStatus.SC_OK)
			throw new RuntimeException("Authentication Failed. Content of failed Response: " + response + " , Status Code : " + response.statusCode());

		Token tokenResponse = response.body().jsonPath().getObject("$", Token.class);
		request.header("Authorization", "Bearer " + tokenResponse.getToken());
	}

	public RestResponse<Books> getBooks() {
		Response response = request.get(Route.books());
		return new GeneralRestResponse<>(Books.class, response);
	}

	public RestResponse<UserAccount> addBook(AddBooksRequest addBooksRequest) {
		Response response = request
			.body(addBooksRequest)
			.post(Route.books());
		return new GeneralRestResponse<>(UserAccount.class, response);
	}

	public Response removeBook(RemoveBookRequest removeBookRequest) {
		return request.body(removeBookRequest).delete(Route.book());
	}

	public RestResponse<UserAccount> getUserAccount(String userId) {
		Response response = request.get(Route.userAccount(userId));
		return new GeneralRestResponse<>(UserAccount.class, response);
	}
}