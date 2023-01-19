package cn.tuyucheng.taketoday.testng.parameter;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Slf4j
public class ParameterUnitTest {

	@Test
	@Parameters({"suite-param"})
	public void test_parameter_1(String param) {
		LOGGER.info("Test one suite param is: {}", param);
	}
}