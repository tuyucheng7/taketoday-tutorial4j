package cn.tuyucheng.taketoday.cucumber.e2e.pages;

import cn.tuyucheng.taketoday.cucumber.e2e.selenium.Wait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ProductListPage {

	WebDriver driver;

	public ProductListPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(how = How.CSS, using = "button.single_add_to_cart_button")
	private WebElement btn_addToCart;

	@FindAll(@FindBy(how = How.CSS, using = ".noo-product-inner"))
	private List<WebElement> product_list;

	@FindBy(how = How.ID, using = "pa_color")
	private WebElement color_li;

	@FindBy(how = How.ID, using = "pa_size")
	private WebElement size_li;

	public void clickOn_addToCart() {
		btn_addToCart.click();
		Wait.untilJqueryIsDone(driver);
	}

	public void select_product(int productNumber) {
		product_list.get(productNumber).click();
	}

	public void select_color(int index) {
		new Select(color_li).selectByIndex(index);
	}

	public void select_size(int index) {
		new Select(size_li).selectByIndex(index);
	}

	public String getProductName(int productNumber) {
		return product_list.get(productNumber).findElement(By.cssSelector("h3")).getText();
	}
}