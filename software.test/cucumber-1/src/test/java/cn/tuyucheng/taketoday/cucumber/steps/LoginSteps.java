package cn.tuyucheng.taketoday.cucumber.steps;

import cn.tuyucheng.taketoday.cucumber.datatable.entity.Credentials;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginSteps {
   private static WebDriver driver;

   @Before
   public static void setUp() {
      System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
   }

   @DataTableType(replaceWithEmptyString = "[blank]")
   public Credentials convert(Map<String, String> entry) {
      Credentials credentials = new Credentials();
      credentials.setUsername(entry.get("username"));
      credentials.setPassword(entry.get("password"));
      return credentials;
   }

   @Given("User is on Home Page")
   public void user_is_on_home_page() {
      driver = new ChromeDriver();
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
      driver.get("https://shop.demoqa.com");
   }

   @When("User Navigate to Login Page")
   public void user_navigate_to_login_page() {
      driver.findElement(By.linkText("Dismiss")).click();
      driver.findElement(By.linkText("My Account")).click();
   }

   @When("User enters Credentials to Login")
   public void user_enters_credentials_to_login(List<Credentials> credentialsList) {
      for (Credentials credentials : credentialsList) {
         driver.findElement(By.id("username")).sendKeys(credentials.getUsername());
         driver.findElement(By.id("password")).sendKeys(credentials.getPassword());
         driver.findElement(By.name("login")).click();
      }
   }

   @When("User enters {string} and {string}")
   public void user_enters_user_name_and_password(String username, String password) {
      driver.findElement(By.id("username")).sendKeys(username);
      driver.findElement(By.id("password")).sendKeys(password);
      driver.findElement(By.name("login")).click();
   }

   @Then("Message displayed Login Successfully")
   public void message_displayed_login_successfully() {
      System.out.println("Login Successfully");
   }

   @When("User Logout from the Application")
   public void user_logout_from_the_application() {
      driver.findElement(By.xpath("//*[text()='Checkout']")).click();
   }

   @Then("Message displayed Logout Successfully")
   public void message_displayed_logout_successfully() {
      System.out.println("Logout Successfully");
   }
}