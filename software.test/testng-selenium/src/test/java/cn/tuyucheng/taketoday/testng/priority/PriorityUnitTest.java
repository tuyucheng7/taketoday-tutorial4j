package cn.tuyucheng.taketoday.testng.priority;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

@Slf4j
public class PriorityUnitTest {
	static {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
	}

	WebDriver driver = new ChromeDriver();

	@Test(priority = 1)
	public void should_closeBrowser() {
		driver.close();
		LOGGER.info("Closing Google Chrome browser");
	}

	@Test(priority = 0)
	public void should_openBrowser() {
		LOGGER.info("Launching Google Chrome browser");
		driver.get("https://demoqa.com");
	}
}