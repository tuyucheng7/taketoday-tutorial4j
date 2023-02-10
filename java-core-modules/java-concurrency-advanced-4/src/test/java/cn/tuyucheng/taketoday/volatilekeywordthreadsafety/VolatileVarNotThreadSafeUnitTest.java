package cn.tuyucheng.taketoday.volatilekeywordthreadsafety;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VolatileVarNotThreadSafeUnitTest {

	@Test
	void whenCalledMainMethod_thenIncrementCount() throws InterruptedException {
		VolatileVarNotThreadSafe.main(null);
		Assertions.assertTrue(VolatileVarNotThreadSafe.getCount() > 0);
	}
}