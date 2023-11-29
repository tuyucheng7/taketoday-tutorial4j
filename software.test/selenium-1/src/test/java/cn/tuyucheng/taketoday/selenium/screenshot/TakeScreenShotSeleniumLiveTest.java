package cn.tuyucheng.taketoday.selenium.screenshot;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.junit.Assert.assertTrue;

public class TakeScreenShotSeleniumLiveTest {

   private static ChromeDriver driver;

   @BeforeClass
   public static void setUp() {
      System.setProperty("webdriver.chrome.driver", resolveResourcePath("chromedriver.exe"));

      ChromeOptions options = new ChromeOptions();
      options.addArguments("--remote-allow-origins=*");
      driver = new ChromeDriver(options);
      driver.manage()
            .timeouts()
            .implicitlyWait(Duration.ofSeconds(5));

      driver.get("http://www.google.com/");
   }

   @AfterClass
   public static void tearDown() {
      driver.close();

      System.clearProperty("webdriver.chrome.driver");
   }

   @Test
   public void whenGoogleIsLoaded_thenCaptureScreenshot() throws IOException {
      takeScreenshot(resolveTestResourcePath("google-home.png"));

      assertTrue(new File(resolveTestResourcePath("google-home.png")).exists());
   }

   @Test
   public void whenGoogleIsLoaded_thenCaptureLogo() throws IOException {
      WebElement logo = driver.findElement(By.id("hplogo"));

      Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000))
            .coordsProvider(new WebDriverCoordsProvider())
            .takeScreenshot(driver, logo);

      ImageIO.write(screenshot.getImage(), "jpg", new File(resolveTestResourcePath("google-logo.png")));
      assertTrue(new File(resolveTestResourcePath("google-logo.png")).exists());
   }

   public void takeScreenshot(String pathname) throws IOException {
      File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
      FileUtils.copyFile(src, new File(pathname));
   }

   private static String resolveResourcePath(String filename) {
      File file = new File("src/main/resources/" + filename);
      return file.getAbsolutePath();
   }

   private static String resolveTestResourcePath(String filename) {
      File file = new File("src/test/resources/" + filename);
      return file.getAbsolutePath();
   }
}