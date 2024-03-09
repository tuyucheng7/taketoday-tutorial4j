package cn.tuyucheng.taketoday.printscreen;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScreenshotLiveTest {

   private Screenshot screenshot = new Screenshot("Screenshot.jpg");
   private File file = new File("Screenshot.jpg");

   @Test
   public void testGetScreenshot() throws Exception {
      screenshot.getScreenshot(2000);
      assertTrue(file.exists());
   }

   @AfterEach
   public void tearDown() throws Exception {
      file.delete();
   }
}