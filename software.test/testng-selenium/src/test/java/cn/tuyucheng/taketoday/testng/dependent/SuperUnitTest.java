package cn.tuyucheng.taketoday.testng.dependent;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class SuperUnitTest {

	@Test
	public void openBrowser() {
		LOGGER.info("Browser Opened");
	}
}