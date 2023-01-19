package cn.tuyucheng.taketoday.concurrent.cyclicbarrier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class CyclicBarrierCountExampleUnitTest {

	@Test
	void whenCyclicBarrier_notCompleted() {
		CyclicBarrierCountExample ex = new CyclicBarrierCountExample(2);
		boolean isCompleted = ex.callTwiceInSameThread();
		assertFalse(isCompleted);
	}
}