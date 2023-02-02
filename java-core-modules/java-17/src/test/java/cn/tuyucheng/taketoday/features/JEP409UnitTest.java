package cn.tuyucheng.taketoday.features;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class JEP409UnitTest {

	static class WeirdTriangle extends JEP409.Triangle {
		@Override
		public int getNumberOfSides() {
			return 40;
		}
	}

	@Test
	void testSealedClass_shouldInvokeRightClass() {
		JEP409.Shape shape = spy(new WeirdTriangle());

		int numberOfSides = getNumberOfSides(shape);

		assertEquals(40, numberOfSides);
		verify(shape).getNumberOfSides();
	}

	int getNumberOfSides(JEP409.Shape shape) {
		return switch (shape) {
			case WeirdTriangle t -> t.getNumberOfSides();
			case JEP409.Circle c -> c.getNumberOfSides();
			case JEP409.Triangle t -> t.getNumberOfSides();
			case JEP409.Rectangle r -> r.getNumberOfSides();
			case JEP409.Square s -> s.getNumberOfSides();
		};
	}
}