package cn.tuyucheng.taketoday.methodmultiplereturnvalues;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultipleReturnValuesUsingCollectionsUnitTest {

	@Test
	void whenUsingList_thenMultipleFieldsAreReturned() {

		List<Number> coordinates = MultipleReturnValuesUsingCollections.getCoordinatesList();
		assertEquals(Integer.valueOf(10), coordinates.get(0));
		assertEquals(Double.valueOf(12.5), coordinates.get(1));
	}

	@Test
	void whenUsingMap_thenMultipleFieldsAreReturned() {

		Map<String, Number> coordinates = MultipleReturnValuesUsingCollections.getCoordinatesMap();
		assertEquals(Integer.valueOf(10), coordinates.get("longitude"));
		assertEquals(Double.valueOf(12.5), coordinates.get("latitude"));
	}
}
