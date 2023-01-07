package cn.tuyucheng.taketoday.finallykeyword;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PitfallsWhenUsingFinallyUnitTest {

	PitfallsWhenUsingFinally instance = new PitfallsWhenUsingFinally();

	@Test
	public void testIgnoresException() {
		String result = instance.disregardsUnCaughtException();
		assertEquals("from finally", result);
	}

	@Test
	public void testIgnoresOtherReturns() {
		String result = instance.ignoringOtherReturns();
		assertEquals("from finally", result);
	}

	@Test(expected = RuntimeException.class)
	public void testThrowsException() {
		instance.throwsException();
	}
}
