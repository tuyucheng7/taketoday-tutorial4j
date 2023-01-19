package cn.tuyucheng.taketoday.testng.dependent;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class GroupDependencyWithXMLUnitTest {

	@Test(groups = {"viewacc"})
	public void viewAcc() {
		LOGGER.info("View Your Dashboard");
	}

	@Test(groups = {"openbrowser"})
	public void openBrowser() {
		LOGGER.info("Browser Opened Successfully");
	}

	@Test(groups = {"login"})
	public void login() {
		LOGGER.info("Login Into The Account");
	}

	@Test(groups = {"logout"})
	public void closeAccount() {
		LOGGER.info("Closing The Account");
	}
}