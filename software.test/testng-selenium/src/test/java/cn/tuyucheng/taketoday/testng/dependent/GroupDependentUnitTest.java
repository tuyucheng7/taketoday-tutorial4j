package cn.tuyucheng.taketoday.testng.dependent;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class GroupDependentUnitTest {

	@Test(dependsOnGroups = "signIn")
	public void viewAcc() {
		LOGGER.info("View Your Dashboard");
	}

	@Test(groups = "signIn")
	public void openBrowser() {
		LOGGER.info("Browser Opened Successfully");
	}

	@Test(groups = "signIn")
	public void test_login() {
		LOGGER.info("Login Into The Account");
	}
}