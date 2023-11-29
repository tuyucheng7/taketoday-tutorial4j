package cn.tuyucheng.taketoday.selenium.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.time.Duration;

public class SeleniumConfig {

   private WebDriver driver;

   public SeleniumConfig() {
      ChromeOptions options = new ChromeOptions();
      options.addArguments("--remote-allow-origins=*");
      driver = new ChromeDriver(options);
      driver.manage()
            .timeouts()
            .implicitlyWait(Duration.ofSeconds(5));
   }

   static {
      System.setProperty("webdriver.chrome.driver", findFile("chromedriver.exe"));
   }

   private static String findFile(String filename) {
      String[] paths = {"", "bin/", "target/classes"}; // if you have chromedriver somewhere else on the path, then put it here.
      for (String path : paths) {
         if (new File(path + filename).exists())
            return path + filename;
      }
      return "";
   }

   public void close() {
      driver.close();
   }

   public void navigateTo(String url) {
      driver.navigate()
            .to(url);
   }

   public void clickElement(WebElement element) {
      element.click();
   }

   public WebDriver getDriver() {
      return driver;
   }
}