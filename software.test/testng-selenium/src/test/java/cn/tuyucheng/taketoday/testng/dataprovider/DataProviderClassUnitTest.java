package cn.tuyucheng.taketoday.testng.dataprovider;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Slf4j
public class DataProviderClassUnitTest {

	@DataProvider(name = "data-provider")
	public Object[][] dataProviderMethod() {
		return new Object[][]{{"First-Value"}, {"Second-Value"}};
	}

	@Test(dataProvider = "data-provider")
	public void shouldUsingParameterFromDataProviderMethod(String val) {
		LOGGER.info("Passed parameter is: {}", val);
	}

	@Test(dataProvider = "data-provider", dataProviderClass = DataProviderClass.class)
	public void shouldUsingParameterFromDataProviderClass(String val) {
		LOGGER.info("Passed parameter is: {}", val);
	}
}