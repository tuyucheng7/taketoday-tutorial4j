package cn.tuyucheng.taketoday.rotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PivotBinarySearchUnitTest {

	@Test
	@DisplayName("givenRotateArray_whenFindElementAndExists_thenReturnIndex")
	void givenRotateArray_whenFindElementAndExists_thenReturnIndex() {
		int[] arr = {5, 6, 7, 8, 9, 10, 1, 2, 3};
		int n = arr.length;
		int findIndex = PivotBinarySearch.pivotBinarySearch(arr, n, 7);
		assertEquals(2, findIndex);
	}

	@Test
	@DisplayName("givenRotateArray_whenFindElementAndNotExists_thenNotFound")
	void givenRotateArray_whenFindElementAndNotExists_thenNotFound() {
		int[] arr = {5, 6, 7, 8, 9, 10, 1, 2, 3};
		int n = arr.length;
		int findIndex = PivotBinarySearch.pivotBinarySearch(arr, n, 4);
		assertEquals(-1, findIndex);
	}

	@Test
	@DisplayName("givenRotateArray_whenFindElementUsingOptimizationAndExists_thenReturnIndex")
	void givenRotateArray_whenFindElementUsingOptimizationAndExists_thenReturnIndex() {
		int[] arr = {4, 5, 6, 7, 8, 9, 1, 2, 3};
		int n = arr.length;
		int key = 6;
		int findIndex = PivotBinarySearch.optimizationPivotBinarySearch(arr, 0, n - 1, key);
		assertEquals(2, findIndex);
	}

	@Test
	@DisplayName("givenRotateArray_whenFindElementUsingOptimizationAndNotExists_thenNotFound")
	void givenRotateArray_whenFindElementUsingOptimizationAndNotExists_thenNotFound() {
		int[] arr = {4, 5, 6, 7, 8, 9, 1, 2, 3};
		int n = arr.length;
		int key = 10;
		int findIndex = PivotBinarySearch.optimizationPivotBinarySearch(arr, 0, n - 1, key);
		assertEquals(-1, findIndex);
	}

	@Test
	@DisplayName("givenRotateArray_whenExistsPairSumToEqualsToInput_thenReturnTrue")
	void givenRotateArray_whenExistsPairSumToEqualsToInput_thenReturnTrue() {
		int[] arr = {11, 15, 6, 8, 9, 10};
		int sum = 16;
		int n = arr.length;
		boolean isExists = PivotBinarySearch.pairInSortedRotated(arr, n, sum);
		assertTrue(isExists);
	}

	@Test
	@DisplayName("givenRotateArray_whenNotExistsPairSumToEqualsToInput_thenReturnFalse")
	void givenRotateArray_whenNotExistsPairSumToEqualsToInput_thenReturnFalse() {
		int[] arr = {11, 15, 6, 8, 9, 10};
		int sum = 22;
		int n = arr.length;
		boolean isExists = PivotBinarySearch.pairInSortedRotated(arr, n, sum);
		assertFalse(isExists);
	}

	@Test
	@DisplayName("givenRotateArray_whenCountPairSumToEqualsToInput_thenReturnCorrect")
	void givenRotateArray_whenCountPairSumToEqualsToInput_thenReturnCorrect() {
		int[] arr = {11, 15, 6, 7, 9, 10};
		int sum = 16;
		int n = arr.length;
		int pairCount = PivotBinarySearch.pairsCountInSortedRotated(arr, n, sum);
		assertEquals(2, pairCount);
	}

	@Test
	@DisplayName("givenRotateArray_whenCountPairSumToEqualsToInputNotExists_thenReturnCorrect")
	void givenRotateArray_whenCountPairSumToEqualsToInputNotExists_thenReturnCorrect() {
		int[] arr = {11, 15, 6, 7, 9, 10};
		int sum = 23;
		int n = arr.length;
		int pairCount = PivotBinarySearch.pairsCountInSortedRotated(arr, n, sum);
		assertEquals(0, pairCount);
	}
}