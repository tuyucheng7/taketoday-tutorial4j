package cn.tuyucheng.taketoday.testng.dependent;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class DependentWithMethodUnitTest {

	@Test(dependsOnMethods = {"openBrowser"})
	public void signIn() {
		LOGGER.info("This will execute second (SignIn)");
	}

	@Test
	public void openBrowser() {
		LOGGER.info("This will execute first (Open Browser)");
	}
}