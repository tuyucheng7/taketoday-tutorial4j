package cn.tuyucheng.taketoday.cucumber.e2e.steps;

import cn.tuyucheng.taketoday.cucumber.e2e.context.TestContext;
import cn.tuyucheng.taketoday.cucumber.e2e.pages.HomePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class HomePageSteps {

   TestContext testContext;
   HomePage homePage;

   public HomePageSteps(TestContext testContext) {
      this.testContext = testContext;
      homePage = testContext.getPageObjectManager().getHomePage();
   }

   @Given("user is on Home Page")
   public void user_is_on_home_page() {
      homePage.navigateTo_homePage();
   }

   @When("he search for {string}")
   public void he_search_for(String searchedKey) {
      homePage.perform_search(searchedKey);
   }
}