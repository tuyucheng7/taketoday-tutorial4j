package cn.tuyucheng.taketoday.fullstack.restassured.serialization;

import cn.tuyucheng.taketoday.fullstack.restassured.serialization.model.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SerializationUnitTest {

	@Test
	void givenAnRectangleObject_shouldCorrectSerializationToFile() {
		Rectangle rectangle = new Rectangle(18, 78);
		SerializationUtil.serializeToFile(rectangle, "rectangleSerialized");
		assertTrue(true);
	}

	@Test
	void whenDeSerializedRectangleObj_thenShouldContainsCorrectFieldValue() {
		Rectangle rectangle = (Rectangle) SerializationUtil.deSerializeFromFileToObject("rectangleSerialized");
		assert rectangle != null;
		assertAll(
			() -> assertEquals(18, rectangle.getHeight()),
			() -> assertEquals(78, rectangle.getWidth())
		);
	}
}