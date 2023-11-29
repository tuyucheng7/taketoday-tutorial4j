package cn.tuyucheng.taketoday.selenium.junit;

import cn.tuyucheng.taketoday.selenium.SeleniumExample;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SeleniumWithJUnitLiveTest {

   private static SeleniumExample seleniumExample;
   private String expectedTitle = "About Baeldung | Baeldung";

   @BeforeClass
   public static void setUp() {
      seleniumExample = new SeleniumExample();
   }

   @AfterClass
   public static void tearDown() throws IOException {
      seleniumExample.closeWindow();
   }

   @Test
   public void whenAboutBaeldungIsLoaded_thenAboutEugenIsMentionedOnPage() {
      seleniumExample.getAboutBaeldungPage();
      String actualTitle = seleniumExample.getTitle();
      assertNotNull(actualTitle);
      assertEquals(expectedTitle, actualTitle);
      assertTrue(seleniumExample.isAuthorInformationAvailable());
   }

}
