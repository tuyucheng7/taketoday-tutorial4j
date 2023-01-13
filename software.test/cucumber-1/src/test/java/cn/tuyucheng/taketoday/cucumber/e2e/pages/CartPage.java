package cn.tuyucheng.taketoday.cucumber.e2e.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class CartPage {

	public CartPage(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}

	@FindBy(how = How.CSS, using = ".cart-button")
	private WebElement btn_cart;

	@FindBy(how = How.CSS, using = ".checkout-button.alt")
	private WebElement btn_continueToCheckout;

	public void clickOn_cart() {
		btn_cart.click();
	}

	public void clickOn_continueToCheckout() {
		btn_continueToCheckout.click();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ignored) {
		}
	}
}