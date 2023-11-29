package cn.tuyucheng.taketoday.cucumber.tags.acceptance.ui.steps;

import cn.tuyucheng.taketoday.cucumber.tags.acceptance.commonutil.ScenarioContextUI;
import cn.tuyucheng.taketoday.cucumber.tags.acceptance.ui.pages.Page;
import cn.tuyucheng.taketoday.cucumber.tags.acceptance.ui.pages.RandomNumberGeneratorPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class RandomNumberGeneratorSteps {

   @Autowired
   private ScenarioContextUI context;

   @Given("we are expecting a random number between min and max")
   public void expectingRandomNumberBetweenMinAndMax() {
   }

   @And("I am on random-number-generator page")
   public void iAmOnRandomNumberGeneratorPage() {
      context.getWebDriver().get(context.getRandomNumberUrl());
      PageFactory.initElements(context.getWebDriver(), RandomNumberGeneratorPage.class);
   }

   @When("^I enter min ([^\"]*)$")
   public void whenIenterMin(String min) {
      Page.getPage(RandomNumberGeneratorPage.class).enterMinField(min);
   }

   @When("^I enter max ([^\"]*)$")
   public void whenIenterMax(String max) {
      Page.getPage(RandomNumberGeneratorPage.class).enterMaxField(max);
   }

   @And("^I press Generate button")
   public void pressScanButton() {
      Page.getPage(RandomNumberGeneratorPage.class).pressGenerateButton();
   }

   @Then("I should receive a random number between {int} and {int}")
   public void iShouldReceiveARandomNumberBetweenAnd(int min, int max) {
      assertTrue(Integer.parseInt(Page.getPage(RandomNumberGeneratorPage.class).getResult()) >= min && Integer.parseInt(Page.getPage(RandomNumberGeneratorPage.class).getResult()) <= max);
   }
}
