package cn.tuyucheng.taketoday.cucumber.e2e.steps;

import cn.tuyucheng.taketoday.cucumber.e2e.context.TestContext;
import cn.tuyucheng.taketoday.cucumber.e2e.managers.FileReaderManager;
import cn.tuyucheng.taketoday.cucumber.e2e.model.Customer;
import cn.tuyucheng.taketoday.cucumber.e2e.pages.CheckoutPage;
import io.cucumber.java.en.When;

public class CheckoutPageSteps {

   TestContext testContext;
   CheckoutPage checkoutPage;

   public CheckoutPageSteps(TestContext testContext) {
      this.testContext = testContext;
      checkoutPage = testContext.getPageObjectManager().getCheckoutPage();
   }

   @When("enter {string} personal details on checkout page")
   public void enter_personal_details_on_checkout_page(String customerName) {
      Customer customer = FileReaderManager.getInstance().getJsonDataReader().getCustomerByName(customerName);
      checkoutPage.fill_personalDetails(customer);
   }

   @When("place the order")
   public void place_the_order() {
      checkoutPage.check_termsAndCondition(true);
      checkoutPage.clickOn_placeOrder();
   }
}