package cn.tuyucheng.taketoday.testng.dependent;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class SingleDependentUnitTest {

	@Test(dependsOnMethods = {"openBrowser"})
	public void signIn() {
		LOGGER.info("User has signed in successfully");
	}

	@Test
	public void openBrowser() {
		LOGGER.info("The browser is opened");
	}

	@Test(dependsOnMethods = {"signIn"})
	public void logout() {
		LOGGER.info("The user logged out successfully");
	}
}