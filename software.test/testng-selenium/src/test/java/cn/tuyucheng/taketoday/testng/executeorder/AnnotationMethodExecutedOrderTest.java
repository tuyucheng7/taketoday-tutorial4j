package cn.tuyucheng.taketoday.testng.executeorder;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.*;

@Slf4j
public class AnnotationMethodExecutedOrderTest {

	@Test
	public void simple_testng_testcase_1() {
		LOGGER.info("5. test method 1");
	}

	@BeforeMethod
	public void beforeMethod() {
		LOGGER.info("4. beforeMethod");
	}

	@AfterMethod
	public void afterMethod() {
		LOGGER.info("6. afterMethod");
	}

	@BeforeClass
	public void beforeClass() {
		LOGGER.info("3. beforeClass");
	}

	@AfterClass
	public void afterClass() {
		LOGGER.info("7. afterClass");
	}

	@BeforeTest
	public void beforeTest() {
		LOGGER.info("2. beforeTest");
	}

	@AfterTest
	public void afterTest() {
		LOGGER.info("8. afterTest");
	}

	@BeforeSuite
	public void beforeSuite() {
		LOGGER.info("1. beforeSuite");
	}

	@AfterSuite
	public void afterSuite() {
		LOGGER.info("9. afterSuite");
	}
}