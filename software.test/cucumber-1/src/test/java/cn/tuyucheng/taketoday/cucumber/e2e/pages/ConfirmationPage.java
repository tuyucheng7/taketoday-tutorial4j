package cn.tuyucheng.taketoday.cucumber.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ConfirmationPage {

	WebDriver driver;

	public ConfirmationPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindAll(@FindBy(how = How.CSS, using = ".order_item"))
	private List<WebElement> product_List;

	public List<String> getProductNames() {
		return product_List.stream()
				.map(element -> element.findElement(By.cssSelector(".product-name")).getText())
				.toList();
	}
}