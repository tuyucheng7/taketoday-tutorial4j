package cn.tuyucheng.taketoday.creational.factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryUnitTest {

	@Test
	void whenUsingFactoryForSquare_thenCorrectObjectReturned() {
		Polygon p;
		PolygonFactory factory = new PolygonFactory();

		// get the shape which has 4 sides
		p = factory.getPolygon(4);
		String result = "The shape with 4 sides is a " + p.getType();

		assertEquals("The shape with 4 sides is a Square", result);
	}

	@Test
	void whenUsingFactoryForOctagon_thenCorrectObjectReturned() {
		Polygon p;
		PolygonFactory factory = new PolygonFactory();

		// get the shape which has 8 sides
		p = factory.getPolygon(8);
		String result = "The shape with 8 sides is a " + p.getType();

		assertEquals("The shape with 8 sides is a Octagon", result);
	}

	@Test
	void whenUsingFactoryForTriangle_thenCorrectObjectReturned() {
		Polygon p;
		PolygonFactory factory = new PolygonFactory();

		// get the shape which has 3 sides
		p = factory.getPolygon(3);
		String result = "The shape with 3 sides is a " + p.getType();

		assertEquals("The shape with 3 sides is a Triangle", result);
	}

	@Test
	void whenUsingFactoryForPentagon_thenCorrectObjectReturned() {
		Polygon p;
		PolygonFactory factory = new PolygonFactory();

		// get the shape which has 5 sides
		p = factory.getPolygon(5);
		String result = "The shape with 5 sides is a " + p.getType();

		assertEquals("The shape with 5 sides is a Pentagon", result);
	}

	@Test
	void whenUsingFactoryForHeptagon_thenCorrectObjectReturned() {
		Polygon p;
		PolygonFactory factory = new PolygonFactory();

		// get the shape which has 7 sides
		p = factory.getPolygon(7);
		String result = "The shape with 7 sides is a " + p.getType();

		assertEquals("The shape with 7 sides is a Heptagon", result);
	}
}