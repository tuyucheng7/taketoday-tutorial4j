package cn.tuyucheng.taketoday.d;

import org.junit.jupiter.api.Test;

class DependencyInversionUnitTest {

	@Test
	void whenNotUsingDI_thenShouldCreateConcreteKeyboard() {
		Windows98Machine windows98Machine = new Windows98Machine();
	}

	@Test
	void whenUsingDI_thenShouldCreateAbstractKeyboard() {
		Monitor monitor = new Monitor();

		Keyboard keyboard = new StandardKeyboard();

		Windows98MachineDI windows98Machine = new Windows98MachineDI(keyboard, monitor);
	}
}