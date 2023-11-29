package cn.tuyucheng.taketoday.cucumber.e2e.selenium;

import cn.tuyucheng.taketoday.cucumber.e2e.managers.FileReaderManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

public class Wait {

   public static void untilJqueryIsDone(WebDriver driver) {
      untilJqueryIsDone(driver, FileReaderManager.getInstance().getConfigFileReader().getImplicitlyWait());
   }

   public static void untilJqueryIsDone(WebDriver driver, long timeoutInSeconds) {
      until(driver, d -> {
         Boolean isJqueryCallDone = (Boolean) ((JavascriptExecutor) driver).executeScript("return jQuery.active===0");
         if (!isJqueryCallDone)
            System.out.println("JQuery call is in Progress");
         return isJqueryCallDone;
      }, timeoutInSeconds);
   }

   public static void untilPageLoadComplete(WebDriver driver) {
      untilPageLoadComplete(driver, FileReaderManager.getInstance().getConfigFileReader().getImplicitlyWait());
   }

   public static void untilPageLoadComplete(WebDriver driver, long timeoutInSeconds) {
      until(driver, d -> {
         boolean isPageLoaded = ((JavascriptExecutor) driver).executeScript("document.readyState").equals("complete");
         if (!isPageLoaded) System.out.println("Document is loading");
         return isPageLoaded;
      }, timeoutInSeconds);
   }

   public static void until(WebDriver driver, Function<WebDriver, Boolean> waitCondition) {
      until(driver, waitCondition, FileReaderManager.getInstance().getConfigFileReader().getImplicitlyWait());
   }

   private static void until(WebDriver driver, Function<WebDriver, Boolean> waitCondition, Long timeoutInSeconds) {
      WebDriverWait webDriverWait = new WebDriverWait(driver, timeoutInSeconds);
      webDriverWait.withTimeout(Duration.ofSeconds(timeoutInSeconds));
      try {
         webDriverWait.until(waitCondition);
      } catch (Exception e) {
         System.out.println(e.getMessage());
      }
   }
}