package cn.tuyucheng.taketoday.rotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindRotateCountUnitTest {

	@Test
	@DisplayName("givenRotateArray_whenCountRotationsUsingLinearSearch_thenCorrect")
	void givenRotateArray_whenCountRotationsUsingLinearSearch_thenCorrect() {
		int[] arr = {15, 18, 2, 3, 6, 12};
		int n = arr.length;
		int rotateCount = FindRotateCount.countRotationsUsingLinearSearch(arr, n);
		assertEquals(2, rotateCount);
	}

	@Test
	@DisplayName("givenNotRotateArray_whenCountRotationsUsingLinearSearch_thenReturnZero")
	void givenNotRotateArray_whenCountRotationsUsingLinearSearch_thenReturnZero() {
		int[] arr = {2, 3, 6, 12, 15, 18};
		int n = arr.length;
		int rotateCount = FindRotateCount.countRotationsUsingLinearSearch(arr, n);
		assertEquals(0, rotateCount);
	}

	@Test
	@DisplayName("givenRotateArray_whenCountRotationsUsingBinarySearch_thenCorrect")
	void givenRotateArray_whenCountRotationsUsingBinarySearch_thenCorrect() {
		int[] arr = {15, 18, 2, 3, 6, 12};
		int n = arr.length;
		int rotateCount = FindRotateCount.countRotationsUsingLinearSearch(arr, n);
		assertEquals(2, rotateCount);
	}

	@Test
	@DisplayName("givenNotRotateArray_whenCountRotationsUsingBinarySearch_thenReturnZero")
	void givenNotRotateArray_whenCountRotationsUsingBinarySearch_thenReturnZero() {
		int[] arr = {2, 3, 6, 12, 15, 18};
		int n = arr.length;
		int rotateCount = FindRotateCount.countRotationsUsingLinearSearch(arr, n);
		assertEquals(0, rotateCount);
	}

	@Test
	@DisplayName("givenRotateArray_whenCountRotationsUsingIterative_thenCorrect")
	void givenRotateArray_whenCountRotationsUsingIterative_thenCorrect() {
		int[] arr = {15, 18, 2, 3, 6, 12};
		int n = arr.length;
		int rotateCount = FindRotateCount.countRotationsUsingLinearSearch(arr, n);
		assertEquals(2, rotateCount);
	}

	@Test
	@DisplayName("givenNotRotateArray_whenCountRotationsUsingIterative_thenReturnZero")
	void givenNotRotateArray_whenCountRotationsUsingIterative_thenReturnZero() {
		int[] arr = {2, 3, 6, 12, 15, 18};
		int n = arr.length;
		int rotateCount = FindRotateCount.countRotationsUsingLinearSearch(arr, n);
		assertEquals(0, rotateCount);
	}
}