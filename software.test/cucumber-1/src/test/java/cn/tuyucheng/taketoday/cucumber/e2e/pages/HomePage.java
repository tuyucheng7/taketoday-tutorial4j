package cn.tuyucheng.taketoday.cucumber.e2e.pages;

import cn.tuyucheng.taketoday.cucumber.e2e.managers.FileReaderManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

   WebDriver driver;

   public HomePage(WebDriver driver) {
      this.driver = driver;
      PageFactory.initElements(driver, this);
   }

   public void perform_search(String searchedKey) {
      driver.navigate().to(FileReaderManager.getInstance().getConfigFileReader().getApplicationUrl() + "/?s=" + searchedKey + "&post_type=product");
   }

   public void navigateTo_homePage() {
      driver.get(FileReaderManager.getInstance().getConfigFileReader().getApplicationUrl());
   }
}