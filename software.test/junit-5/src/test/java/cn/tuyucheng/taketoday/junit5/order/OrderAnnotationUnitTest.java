package cn.tuyucheng.taketoday.junit5.order;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
class OrderAnnotationUnitTest {

	private static final StringBuilder output = new StringBuilder();

	@Test
	@Order(1)
	void firstTest() {
		output.append("a");
	}

	@Test
	@Order(2)
	void secondTest() {
		output.append("b");
	}

	@Test
	@Order(3)
	void thirdTest() {
		output.append("c");
	}

	@AfterAll
	static void assertOutput() {
		assertEquals("abc", output.toString());
	}
}