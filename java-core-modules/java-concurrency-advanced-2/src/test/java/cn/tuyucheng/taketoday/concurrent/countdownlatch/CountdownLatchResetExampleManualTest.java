package cn.tuyucheng.taketoday.concurrent.countdownlatch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CountdownLatchResetExampleManualTest {

	@Test
	void whenCountDownLatch_noReset() {
		CountdownLatchResetExample ex = new CountdownLatchResetExample(7, 20);
		int lineCount = ex.countWaits();
		assertTrue(lineCount <= 7);
	}
}