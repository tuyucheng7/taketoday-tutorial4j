package cn.tuyucheng.taketoday.creational.singleton;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingletonUnitTest {

	@Test
	/**
	 * Although there is absolutely no way to determine whether
	 * a class is Singleton, in this test case, we will just
	 * check for two objects if they point to same instance or
	 * not. We will also check for their hashcode.
	 */
	void whenGettingMultipleObjects_thenAllPointToSame() {
		// first object
		Singleton obj1 = Singleton.getInstance();

		// Second object
		Singleton obj2 = Singleton.getInstance();

		assertTrue(obj1 == obj2);
		assertEquals(obj1.hashCode(), obj2.hashCode());
	}
}