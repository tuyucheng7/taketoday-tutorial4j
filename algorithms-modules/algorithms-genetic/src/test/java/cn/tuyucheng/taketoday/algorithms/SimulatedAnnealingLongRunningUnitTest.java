package cn.tuyucheng.taketoday.algorithms;

import cn.tuyucheng.taketoday.algorithms.ga.annealing.SimulatedAnnealing;
import org.junit.Assert;
import org.junit.Test;

public class SimulatedAnnealingLongRunningUnitTest {

	@Test
	public void testSimulateAnnealing() {
		Assert.assertTrue(SimulatedAnnealing.simulateAnnealing(10, 1000, 0.9) > 0);
	}

}
