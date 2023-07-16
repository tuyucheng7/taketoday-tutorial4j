package cn.tuyucheng.taketoday.selenium.webdriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeleniumWebDriverLiveTest {

   private WebDriver driver;

   private static final String URL = "https://duckduckgo.com/";
   private static final String INPUT_ID = "searchbox_input__bEGm3";

   @BeforeEach
   void setUp() {
      WebDriverManager.chromedriver().setup();
      ChromeOptions options = new ChromeOptions();
      options.addArguments("--remote-allow-origins=*");
      driver = new ChromeDriver(options);
   }

   @AfterEach
   void tearDown() {
      driver.quit();
   }

   @Test
   void givenDuckDuckGoHomePage_whenInputHelloWorld_thenInputValueIsHelloWorld() {
      driver.get(URL);
      WebElement inputElement = driver.findElement(By.className(INPUT_ID));
      inputElement.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
      inputElement.sendKeys("Hello World!");

      String inputValue = inputElement.getAttribute("value");
      assertEquals("Hello World!", inputValue);
   }
}