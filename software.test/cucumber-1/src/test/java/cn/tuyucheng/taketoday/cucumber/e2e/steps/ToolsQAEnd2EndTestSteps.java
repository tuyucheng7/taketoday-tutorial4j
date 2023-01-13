package cn.tuyucheng.taketoday.cucumber.e2e.steps;

import cn.tuyucheng.taketoday.cucumber.e2e.managers.PageObjectManager;
import cn.tuyucheng.taketoday.cucumber.e2e.managers.WebDriverManager;
import cn.tuyucheng.taketoday.cucumber.e2e.pages.CartPage;
import cn.tuyucheng.taketoday.cucumber.e2e.pages.CheckoutPage;
import cn.tuyucheng.taketoday.cucumber.e2e.pages.HomePage;
import cn.tuyucheng.taketoday.cucumber.e2e.pages.ProductListPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

public class ToolsQAEnd2EndTestSteps {

	// private static WebDriver driver;
	// private static PageObjectManager pageObjectManager;
	// private static HomePage homePage;
	// private static ProductListPage productListPage;
	// private static CartPage cartPage;
	// private static CheckoutPage checkoutPage;
	// private static WebDriverManager webDriverManager;
	//
	// @Given("user is on Home Page")
	// public void user_is_on_home_page() {
	//     webDriverManager = new WebDriverManager();
	//     driver = webDriverManager.getDriver();
	//     pageObjectManager = new PageObjectManager(driver);
	//     homePage = pageObjectManager.getHomePage();
	//     homePage.navigateTo_homePage();
	// }
	//
	// @When("he search for {string}")
	// public void he_search_for(String searchedKey) {
	//     homePage.perform_search(searchedKey);
	// }
	//
	// @When("choose to buy the first item")
	// public void choose_to_buy_the_first_item() {
	//     productListPage = pageObjectManager.getProductListPage();
	//     productListPage.select_product(0);
	//     productListPage.select_color(1);
	//     productListPage.select_size(1);
	//     productListPage.clickOn_addToCart();
	// }
	//
	// @When("moves to checkout from mini cart")
	// public void moves_to_checkout_from_mini_cart() {
	//     cartPage = pageObjectManager.getCartPage();
	//     cartPage.clickOn_cart();
	//     cartPage.clickOn_continueToCheckout();
	// }
	//
	// @When("enter personal details on checkout page")
	// public void enter_personal_details_on_checkout_page() {
	//     checkoutPage = pageObjectManager.getCheckoutPage();
	//     checkoutPage.fill_personalDetails();
	// }
	//
	// @When("place the order")
	// public void place_the_order() {
	//     checkoutPage.check_termsAndCondition(true);
	//     checkoutPage.clickOn_placeOrder();
	//     webDriverManager.closeDriver();
	// }
}