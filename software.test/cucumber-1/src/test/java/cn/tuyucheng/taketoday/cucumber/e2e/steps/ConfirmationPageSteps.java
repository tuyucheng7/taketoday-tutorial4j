package cn.tuyucheng.taketoday.cucumber.e2e.steps;

import cn.tuyucheng.taketoday.cucumber.e2e.context.TestContext;
import cn.tuyucheng.taketoday.cucumber.e2e.enums.Context;
import cn.tuyucheng.taketoday.cucumber.e2e.pages.ConfirmationPage;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfirmationPageSteps {

   TestContext testContext;
   ConfirmationPage confirmationPage;

   public ConfirmationPageSteps(TestContext testContext) {
      this.testContext = testContext;
      confirmationPage = testContext.getPageObjectManager().getConfirmationPage();
   }

   @Then("verify the order details")
   public void verify_the_order_details() {
      String productName = (String) testContext.scenarioContext.getContext(Context.PRODUCT_NAME);
      assertTrue(confirmationPage.getProductNames().stream()
            .filter(x -> x.contains(productName.toLowerCase())).findFirst()
            .get()
            .length() > 0);
   }
}