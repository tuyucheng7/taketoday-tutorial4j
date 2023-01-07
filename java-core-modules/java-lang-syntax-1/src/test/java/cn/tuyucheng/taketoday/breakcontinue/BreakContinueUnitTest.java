package cn.tuyucheng.taketoday.breakcontinue;

import org.junit.Test;

import static cn.tuyucheng.taketoday.breakcontinue.BreakContinue.labeledBreak;
import static cn.tuyucheng.taketoday.breakcontinue.BreakContinue.labeledContinue;
import static cn.tuyucheng.taketoday.breakcontinue.BreakContinue.unlabeledBreak;
import static cn.tuyucheng.taketoday.breakcontinue.BreakContinue.unlabeledBreakNestedLoops;
import static cn.tuyucheng.taketoday.breakcontinue.BreakContinue.unlabeledContinue;
import static org.junit.Assert.assertEquals;

public class BreakContinueUnitTest {

	@Test
	public void whenUnlabeledBreak_ThenEqual() {
		assertEquals(4, unlabeledBreak());
	}

	@Test
	public void whenUnlabeledBreakNestedLoops_ThenEqual() {
		assertEquals(2, unlabeledBreakNestedLoops());
	}

	@Test
	public void whenLabeledBreak_ThenEqual() {
		assertEquals(1, labeledBreak());
	}

	@Test
	public void whenUnlabeledContinue_ThenEqual() {
		assertEquals(5, unlabeledContinue());
	}

	@Test
	public void whenLabeledContinue_ThenEqual() {
		assertEquals(3, labeledContinue());
	}

}
