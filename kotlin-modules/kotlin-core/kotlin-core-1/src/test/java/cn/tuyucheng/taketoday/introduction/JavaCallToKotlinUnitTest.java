package cn.tuyucheng.taketoday.introduction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaCallToKotlinUnitTest {
	@Test
	void givenKotlinClass_whenCallFromJava_shouldProduceResults() {
		// when
		int res = new MathematicsOperations().addTwoNumbers(2, 4);

		// then
		assertEquals(6, res);
	}
}