package cn.tuyucheng.taketoday.cucumber.e2e.steps;

import cn.tuyucheng.taketoday.cucumber.e2e.context.TestContext;
import cn.tuyucheng.taketoday.cucumber.e2e.enums.Context;
import cn.tuyucheng.taketoday.cucumber.e2e.pages.ProductListPage;
import io.cucumber.java.en.When;

public class ProductPageSteps {

   TestContext testContext;
   ProductListPage productListPage;

   public ProductPageSteps(TestContext testContext) {
      this.testContext = testContext;
      productListPage = testContext.getPageObjectManager().getProductListPage();
   }

   @When("choose to buy the first item")
   public void choose_to_buy_the_first_item() {
      String productName = productListPage.getProductName(0);
      testContext.getScenarioContext().setContext(Context.PRODUCT_NAME, productName);

      productListPage.select_product(0);
      productListPage.select_color(1);
      productListPage.select_size(1);
      productListPage.clickOn_addToCart();
   }
}