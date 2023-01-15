package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.steps;

import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.api.model.requests.AuthorizationRequest;
import cn.tuyucheng.taketoday.fullstack.restassured.cucumber.context.TestContext;
import io.cucumber.java.en.Given;

public class AccountSteps extends BaseStep {

	public AccountSteps(TestContext testContext) {
		super(testContext);
	}

	@Given("I am an authorized user")
	public void i_am_an_authorized_user() {
		AuthorizationRequest authRequest = new AuthorizationRequest("TakeToday-Test", "Test@@123");
		getEndPoints().authenticateUser(authRequest);
	}
}