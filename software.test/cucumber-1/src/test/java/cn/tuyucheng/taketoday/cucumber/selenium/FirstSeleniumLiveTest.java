package cn.tuyucheng.taketoday.cucumber.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

class FirstSeleniumLiveTest {

   @BeforeEach
   void setUp() {
      System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
   }

   @Test
   void givenLoginPage_whenAccess_thenGetSomeElement() {
      // Create a new instance of the ChromeDriver.
      String studentName = "";
      WebDriver driver = new ChromeDriver();

      // Put an Implicit wait, this means that any search for elements on the page
      // could take the time the implicit wait is set for before throwing exception
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

      // Launch the Online Store Website
      driver.get("https://shop.demoqa.com");

      // Find the element that's linkText is 'Dismiss'(Dismiss) and 'My Account'(My Account).
      driver.findElement(By.linkText("Dismiss")).click();
      driver.findElement(By.linkText("My Account")).click();

      // Find the element that's ID attribute is 'username' (Username).
      // Enter Username on the element found by above desc.
      driver.findElement(By.id("username")).sendKeys("testuser_1");

      // Find the element that's ID attribute is 'password' (Password)
      // Enter Password on the element found by the above desc.
      driver.findElement(By.id("password")).sendKeys("Test@123");

      // Now submit the form. WebDriver will find the form for us from the element.
      driver.findElement(By.name("login")).click();

      // Print a Log In message to the screen.
      System.out.println("Login Successfully");

      // Find the element that's ID attribute is 'account_logout' (Log Out)
      driver.findElement(By.xpath("//*[text()='Checkout']")).click();
      // driver.findElement(By.xpath(".//*[@id='account_logout']/a")).click();

      // Print a Log In message to the screen.
      System.out.println("Logout Successfully");

      // Close the driver.
      driver.quit();
   }
}