package cn.tuyucheng.taketoday.cucumber.e2e.pages;

import cn.tuyucheng.taketoday.cucumber.e2e.model.Customer;
import cn.tuyucheng.taketoday.cucumber.e2e.selenium.Wait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class CheckoutPage {

	WebDriver driver;

	public CheckoutPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(how = How.CSS, using = "#billing_first_name")
	private WebElement txt_firstName;

	@FindBy(how = How.CSS, using = "#billing_last_name")
	private WebElement txt_lastName;

	@FindBy(how = How.CSS, using = "#billing_email")
	private WebElement txt_email;

	@FindBy(how = How.CSS, using = "#billing_phone")
	private WebElement txt_phone;

	@FindBy(how = How.CSS, using = "#billing_city")
	private WebElement txt_city;

	@FindBy(how = How.CSS, using = "#billing_address_1")
	private WebElement txt_address;

	@FindBy(how = How.CSS, using = "#billing_postcode")
	private WebElement txt_postCode;

	@FindBy(how = How.CSS, using = "#terms.input-checkbox")
	private WebElement cbx_acceptTermsAndCondition;

	@FindBy(how = How.CSS, using = "#place_order")
	private WebElement btn_placeOrder;

	public void enter_firstName(String firstName) {
		txt_firstName.sendKeys(firstName);
	}

	public void enter_lastName(String lastName) {
		txt_lastName.sendKeys(lastName);
	}

	public void enter_email(String email) {
		txt_email.sendKeys(email);
	}

	public void enter_phone(String phone) {
		txt_phone.sendKeys(phone);
	}

	public void enter_city(String city) {
		txt_city.sendKeys(city);
	}

	public void enter_address(String address) {
		txt_address.sendKeys(address);
	}

	public void enter_postCode(String postCode) {
		txt_postCode.sendKeys(postCode);
	}

	public void check_termsAndCondition(boolean value) {
		if (value) cbx_acceptTermsAndCondition.click();
	}

	public void clickOn_placeOrder() {
		btn_placeOrder.submit();
		Wait.untilJqueryIsDone(driver);
		Wait.untilPageLoadComplete(driver);
	}

	public void fill_personalDetails(Customer customer) {
		enter_firstName(customer.getFirstName());
		enter_lastName(customer.getLastName());
		enter_phone(customer.getPhoneNumber().getMob());
		enter_email(customer.getEmailAddress());
		enter_city(customer.getAddress().getCity());
		enter_address(customer.getAddress().getStreetAddress());
		enter_postCode(customer.getAddress().getPostCode());
	}
}