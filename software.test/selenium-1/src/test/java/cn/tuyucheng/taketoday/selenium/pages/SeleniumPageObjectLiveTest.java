package cn.tuyucheng.taketoday.selenium.pages;

import cn.tuyucheng.taketoday.selenium.config.SeleniumConfig;
import cn.tuyucheng.taketoday.selenium.models.BaeldungAbout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SeleniumPageObjectLiveTest {

   private SeleniumConfig config;
   private BaeldungHomePage homePage;
   private BaeldungAbout about;

   @Before
   public void setUp() {
      config = new SeleniumConfig();
      homePage = new BaeldungHomePage(config);
      about = new BaeldungAbout(config);
   }

   @After
   public void teardown() {
      config.close();
   }

   @Test
   public void givenHomePage_whenNavigate_thenTitleMatch() {
      homePage.navigate();
      assertThat(homePage.getPageTitle(), is("Baeldung"));
   }

   @Test
   public void givenHomePage_whenNavigate_thenShouldBeInStartHere() {
      homePage.navigate();
      StartHerePage startHerePage = homePage.clickOnStartHere();
      assertThat(startHerePage.getPageTitle(), is("Start Here"));
   }

   @Test
   public void givenAboutPage_whenNavigate_thenTitleMatch() {
      about.navigateTo();
      assertThat(about.getPageTitle(), is("About Baeldung"));
   }
}
