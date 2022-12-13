package cn.tuyucheng.taketoday.stream;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamApiUnitTest {

	@Test
	void givenList_whenGetLastElementUsingReduce_thenReturnLastElement() {
		List<String> valueList = new ArrayList<>();
		valueList.add("Joe");
		valueList.add("John");
		valueList.add("Sean");

		String last = StreamApi.getLastElementUsingReduce(valueList);

		assertEquals("Sean", last);
	}

	@Test
	void givenInfiniteStream_whenGetInfiniteStreamLastElementUsingReduce_thenReturnLastElement() {
		int last = StreamApi.getInfiniteStreamLastElementUsingReduce();
		assertEquals(19, last);
	}

	@Test
	void givenListAndCount_whenGetLastElementUsingSkip_thenReturnLastElement() {
		List<String> valueList = new ArrayList<>();
		valueList.add("Joe");
		valueList.add("John");
		valueList.add("Sean");

		String last = StreamApi.getLastElementUsingSkip(valueList);

		assertEquals("Sean", last);
	}
}