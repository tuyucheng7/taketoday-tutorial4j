package cn.tuyucheng.taketoday.java10.list;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CopyListServiceUnitTest {

	@Test
	void whenModifyCopyOfList_thenThrowsException() {
		List<Integer> copyList = List.copyOf(Arrays.asList(1, 2, 3, 4));
		assertThrows(UnsupportedOperationException.class, () -> copyList.add(4));
	}
}