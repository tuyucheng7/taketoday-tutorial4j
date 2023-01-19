package cn.tuyucheng.taketoday.testng.parameter;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Slf4j
public class SimpleParameterUnitTest {

	@Test
	@Parameters({"a", "b"})
	public void givenTwoParameters_whenSumIsCalled_thenShouldCorrect(int a, int b) {
		int finalSum = a + b;
		LOGGER.info("The final sum of the given values is " + finalSum);
	}

	@Test
	@Parameters({"a", "b"})
	public void givenTwoParameters_whenDiffIsCalled_thenShouldCorrect(int a, int b) {
		int finalDiff = a - b;
		LOGGER.info("The final difference of the given values is " + finalDiff);
	}
}