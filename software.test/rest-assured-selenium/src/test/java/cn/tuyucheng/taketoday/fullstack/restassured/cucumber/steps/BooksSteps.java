package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.steps;

import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.RestResponse;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.Book;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.requests.AddBooksRequest;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.requests.ISBN;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.requests.RemoveBookRequest;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.responses.Books;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.responses.UserAccount;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.context.TestContext;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.enums.Context;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class BooksSteps extends BaseStep {

	public BooksSteps(TestContext testContext) {
		super(testContext);
	}

	@Given("A list of books are available")
	public void a_list_of_books_are_available() {
		RestResponse<Books> booksResponse = getEndPoints().getBooks();
		Book book = booksResponse.getBody().getBooks().get(0);
		getScenarioContext().setContext(Context.BOOK, book);
	}

	@When("I add a book to my reading list")
	public void i_add_a_book_to_my_reading_list() {
		Book book = (Book) getScenarioContext().getContext(Context.BOOK);
		String userId = (String) getScenarioContext().getContext(Context.USER_ID);

		ISBN isbn = new ISBN(book.getIsbn());
		AddBooksRequest addBooksRequest = new AddBooksRequest(userId, isbn);
		RestResponse<UserAccount> userAccountResponse = getEndPoints().addBook(addBooksRequest);
		getScenarioContext().setContext(Context.USER_ACCOUNT_RESPONSE, userAccountResponse);
	}

	@When("I remove a book from my reading list")
	public void i_remove_a_book_from_my_reading_list() {
		Book book = (Book) getScenarioContext().getContext(Context.BOOK);
		String userId = (String) getScenarioContext().getContext(Context.USER_ID);
		RemoveBookRequest removeBookRequest = new RemoveBookRequest(userId, book.getIsbn());

		Response response = getEndPoints().removeBook(removeBookRequest);
		getScenarioContext().setContext(Context.BOOK_REMOVE_RESPONSE, response);
	}
}