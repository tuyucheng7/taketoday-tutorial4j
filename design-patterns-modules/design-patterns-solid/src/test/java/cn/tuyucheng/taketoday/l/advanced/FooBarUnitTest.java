package cn.tuyucheng.taketoday.l.advanced;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FooBarUnitTest {

	@Test
	void whenCreateFoo_thenShouldCreateFoo() {
		Foo foo = new Bar();

		foo.doStuff(3);
		assertThrows(IllegalArgumentException.class, () -> foo.doStuff(0));

		foo.doOtherStuff(1);
		assertThrows(IllegalArgumentException.class, () -> foo.doOtherStuff(10));
	}

	@Test
	void whenCreateBar_thenShouldCreateBar() {
		Bar foo = new Bar();

		foo.doStuff(10);
		assertThrows(IllegalArgumentException.class, () -> foo.doStuff(11));

		foo.doOtherStuff(1);
		assertThrows(IllegalArgumentException.class, () -> foo.doOtherStuff(4));

		assertEquals(10, foo.generateNumber());
	}
}