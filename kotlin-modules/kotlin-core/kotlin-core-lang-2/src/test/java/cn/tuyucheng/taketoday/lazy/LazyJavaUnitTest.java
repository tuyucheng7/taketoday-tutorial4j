package cn.tuyucheng.taketoday.lazy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class LazyJavaUnitTest {

	@Test
	void giveHeavyClass_whenInitLazy_thenShouldReturnInstanceOnFirstCall() {
		// when
		ClassWithHeavyInitialization classWithHeavyInitialization = ClassWithHeavyInitialization.getInstance();
		ClassWithHeavyInitialization classWithHeavyInitialization2 = ClassWithHeavyInitialization.getInstance();

		// then
		assertSame(classWithHeavyInitialization, classWithHeavyInitialization2);
	}
}