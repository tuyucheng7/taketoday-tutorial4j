package cn.tuyucheng.taketoday.monad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonadSampleUnitTest {

	@Test
	void whenNotUsingMonad_shouldBeOk() {
		MonadSample1 test = new MonadSample1();
		assertEquals(6.0, test.apply(2), 0.000);
	}

	@Test
	void whenNotUsingMonadButUsingTempVars_shouldBeOk() {
		MonadSample2 test = new MonadSample2();
		assertEquals(6.0, test.apply(2), 0.000);
	}

	@Test
	void whenUsingMonad_shouldBeOk() {
		MonadSample3 test = new MonadSample3();
		assertEquals(6.0, test.apply(2), 0.000);
	}

	@Test
	void whenTestingMonadProperties_shouldBeOk() {
		MonadSample4 test = new MonadSample4();
		assertTrue(test.leftIdentity());
		assertTrue(test.rightIdentity());
		assertTrue(test.associativity());
	}

	@Test
	void whenBreakingMonadProperties_shouldBeFalse() {
		MonadSample5 test = new MonadSample5();
		assertFalse(test.fail());
	}
}