package cn.tuyucheng.taketoday.testng.suite;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Slf4j
public class FirstTestNGUnitTest {

	WebDriver driver;

	@BeforeMethod
	public void beforeMethod() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
		driver = new ChromeDriver();
		LOGGER.info("Starting Test on Chrome Browser");
	}

	@Test
	public void simple_testng_testMethod() {
		LOGGER.info("Launching Google Chrome browser");

		driver.get("https://www.toolsqa.com");
		String testTitle = "Tools QA";
		String originalTitle = driver.getTitle();

		assertEquals(originalTitle, testTitle);
	}

	@AfterMethod
	public void afterMethod() {
		driver.close();
		LOGGER.info("Finished Test on Chrome Browser");
	}
}