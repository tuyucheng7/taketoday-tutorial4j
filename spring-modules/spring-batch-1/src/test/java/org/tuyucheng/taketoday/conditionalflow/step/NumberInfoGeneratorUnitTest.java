package org.tuyucheng.taketoday.conditionalflow.step;

import org.junit.jupiter.api.Test;
import org.tuyucheng.taketoday.conditionalflow.model.NumberInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NumberInfoGeneratorUnitTest {

	@Test
	void givenArray_whenGenerator_correctOrderAndValue() {
		int[] numbers = new int[]{1, -2, 4, -10};
		NumberInfoGenerator numberGenerator = new NumberInfoGenerator(numbers);
		assertEquals(new NumberInfo(numbers[0]), numberGenerator.read());
		assertEquals(new NumberInfo(numbers[1]), numberGenerator.read());
		assertEquals(new NumberInfo(numbers[2]), numberGenerator.read());
		assertEquals(new NumberInfo(numbers[3]), numberGenerator.read());
		assertNull(numberGenerator.read());
	}
}