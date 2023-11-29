package cn.tuyucheng.taketoday.selenium.models;

import cn.tuyucheng.taketoday.selenium.config.SeleniumConfig;
import cn.tuyucheng.taketoday.selenium.pages.BaeldungAboutPage;
import org.openqa.selenium.support.PageFactory;

public class BaeldungAbout {

   private SeleniumConfig config;

   public BaeldungAbout(SeleniumConfig config) {
      this.config = config;
      PageFactory.initElements(config.getDriver(), BaeldungAboutPage.class);
   }

   public void navigateTo() {
      config.navigateTo("http://www.baeldung.com/about/");
   }

   public String getPageTitle() {
      return BaeldungAboutPage.title.getText();
   }
}
