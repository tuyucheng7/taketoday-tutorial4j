package cn.tuyucheng.taketoday.testng.parameter;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Slf4j
public class OptionalParamUnitTest {

	@Test
	@Parameters("message")
	public void whenNotPassMessageParam_thenShouldUsingTheDefaultValue(@Optional("Optional Parameter Selected") String message) {
		LOGGER.info("The message values is: {}", message);
	}
}