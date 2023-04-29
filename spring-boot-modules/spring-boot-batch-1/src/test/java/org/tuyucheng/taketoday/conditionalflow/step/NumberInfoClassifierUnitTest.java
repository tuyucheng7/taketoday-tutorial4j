package org.tuyucheng.taketoday.conditionalflow.step;

import org.junit.jupiter.api.Test;
import org.tuyucheng.taketoday.conditionalflow.model.NumberInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberInfoClassifierUnitTest {

	@Test
	void givenNumberInfo_whenProcess_thenConvertsToInteger() throws Exception {
		NumberInfoClassifier nic = new NumberInfoClassifier();
		assertEquals(Integer.valueOf(4), nic.process(NumberInfo.from(4)));
		assertEquals(Integer.valueOf(-4), nic.process(NumberInfo.from(-4)));
	}
}