package cn.tuyucheng.taketoday.lcm;

import org.junit.Assert;
import org.junit.Test;

import static cn.tuyucheng.taketoday.lcm.SimpleAlgorithm.lcm;

public class SimpleAlgorithmUnitTest {

	@Test
	public void testLCM() {
		Assert.assertEquals(36, lcm(12, 18));
	}

}
