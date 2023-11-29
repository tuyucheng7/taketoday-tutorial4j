package cn.tuyucheng.taketoday.cucumber.e2e.steps;

import cn.tuyucheng.taketoday.cucumber.e2e.context.TestContext;
import cn.tuyucheng.taketoday.cucumber.e2e.pages.CartPage;
import io.cucumber.java.en.When;

public class CartPageSteps {

   TestContext testContext;
   CartPage cartPage;

   public CartPageSteps(TestContext testContext) {
      this.testContext = testContext;
      cartPage = testContext.getPageObjectManager().getCartPage();
   }

   @When("moves to checkout from mini cart")
   public void moves_to_checkout_from_mini_cart() {
      cartPage.clickOn_cart();
      cartPage.clickOn_continueToCheckout();
   }
}