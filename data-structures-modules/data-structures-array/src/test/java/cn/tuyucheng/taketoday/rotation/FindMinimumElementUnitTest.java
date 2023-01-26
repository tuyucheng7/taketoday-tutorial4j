package cn.tuyucheng.taketoday.rotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindMinimumElementUnitTest {

	@Test
	@DisplayName("givenRotateArray_whenFindMinimumElement_thenFound")
	void givenRotateArray_whenFindMinimumElement_thenFound() {
		int[] arr = {5, 6, 1, 2, 3, 4};
		int n = arr.length;
		int findedIndex = FindMinimumElement.findMinUsingBinarySearch(arr, 0, n - 1);
		assertEquals(1, findedIndex);
	}

	@Test
	@DisplayName("givenRotateArrayWithDuplicateElement_whenFindMinimum_thenFound")
	void givenRotateArrayWithDuplicateElement_whenFindMinimum_thenFound() {
		int[] arr = {5, 6, 1, 2, 3, 4};
		int n = arr.length;
		int findedIndex = FindMinimumElement.findMinUsingBinarySearch(arr, 0, n - 1);
		assertEquals(1, findedIndex);
	}
}