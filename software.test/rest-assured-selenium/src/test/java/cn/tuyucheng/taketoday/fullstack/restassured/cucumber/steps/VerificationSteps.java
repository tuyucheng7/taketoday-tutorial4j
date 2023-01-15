package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.steps;

import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.RestResponse;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.Book;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.responses.UserAccount;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.context.TestContext;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.enums.Context;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VerificationSteps extends BaseStep {

	public VerificationSteps(TestContext testContext) {
		super(testContext);
	}

	@Then("The book is added")
	public void the_book_is_added() {
		Book book = (Book) getScenarioContext().getContext(Context.BOOK);
		RestResponse<UserAccount> userAccountResponse = (RestResponse<UserAccount>) getScenarioContext().getContext(Context.USER_ACCOUNT_RESPONSE);

		assertTrue(userAccountResponse.isSuccessful());
		assertEquals(201, userAccountResponse.getStatusCode());

		assertEquals(book.getIsbn(), userAccountResponse.getBody().getBooks().get(0).getIsbn());
	}

	@Then("The book is removed")
	public void the_book_is_removed() {
		String userId = (String) getScenarioContext().getContext(Context.USER_ID);
		Response response = (Response) getScenarioContext().getContext(Context.BOOK_REMOVE_RESPONSE);

		assertEquals(204, response.getStatusCode());

		RestResponse<UserAccount> userAccountResponse = getEndPoints().getUserAccount(userId);

		assertEquals(200, userAccountResponse.getStatusCode());
		assertEquals(0, userAccountResponse.getBody().getBooks().size());
	}
}