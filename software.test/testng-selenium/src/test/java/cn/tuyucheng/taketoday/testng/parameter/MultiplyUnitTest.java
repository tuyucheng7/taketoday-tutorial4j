package cn.tuyucheng.taketoday.testng.parameter;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Slf4j
public class MultiplyUnitTest {

	@Test
	@Parameters({"a", "b"})
	public void givenTwoParameters_whenMultiplyIsCalled_thenShouldCorrect(int a, int b) {
		int prod = a * b;
		LOGGER.info("The Product Of a and b is " + prod);
	}
}