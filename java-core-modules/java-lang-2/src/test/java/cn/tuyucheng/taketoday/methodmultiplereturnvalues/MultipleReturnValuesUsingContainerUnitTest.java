package cn.tuyucheng.taketoday.methodmultiplereturnvalues;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultipleReturnValuesUsingContainerUnitTest {

	@Test
	void whenUsingContainerClass_thenMultipleFieldsAreReturned() {

		Coordinates coordinates = MultipleReturnValuesUsingContainer.getCoordinates();

		assertEquals(10, coordinates.getLongitude());
		assertEquals(12.5, coordinates.getLatitude());
		assertEquals("home", coordinates.getPlaceName());
	}
}
