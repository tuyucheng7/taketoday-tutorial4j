package cn.tuyucheng.taketoday.testng.groups;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Slf4j
public class GroupsUnitTest {

	WebDriver driver;
	String title = "ToolsQA";

	@Test
	public void test_starting_point() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
		driver = new ChromeDriver();
		LOGGER.info("This is the starting point of the test");
		driver.get("https://demoqa.com");
	}

	@Test(groups = "demo1", priority = 1)
	public void test_check_title() {
		String testTitle = "ToolsQA";
		String originalTitle = driver.getTitle();
		assertEquals(originalTitle, testTitle);
	}

	@Test(groups = "demo2", priority = 2)
	public void test_check_element() {
		driver.findElement(By.className("banner-image")).click();
		LOGGER.info("Home Page heading is displayed");
	}
}