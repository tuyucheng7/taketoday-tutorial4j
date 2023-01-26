package cn.tuyucheng.taketoday.arrangement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class RearrangeArrayUnitTest {

	@Test
	@DisplayName("givenArray_whenRearrangeArray_thenSuccess")
	void givenArray_whenRearrangeArray_thenSuccess() {
		int[] arr = {-1, -1, 6, 1, 9, 3, 2, -1, 4, -1};
		int n = arr.length;
		RearrangeArray.fixArray(arr, n);
		int[] expected = {-1, 1, 2, 3, 4, -1, 6, -1, -1, 9};
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenRearrangeArrayOptimization_thenSuccess")
	void givenArray_whenRearrangeArrayOptimization_thenSuccess() {
		int[] arr = {-1, -1, 6, 1, 9, 3, 2, -1, 4, -1};
		int n = arr.length;
		RearrangeArray.fixArrayOptimization(arr, n);
		int[] expected = {-1, 1, 2, 3, 4, -1, 6, -1, -1, 9};
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenRearrangeArrayUsingSet_thenSuccess")
	void givenArray_whenRearrangeArrayUsingSet_thenSuccess() {
		int[] arr = {-1, -1, 6, 1, 9, 3, 2, -1, 4, -1};
		int n = arr.length;
		RearrangeArray.fixArrayUsingSet(arr, n);
		int[] expected = {-1, 1, 2, 3, 4, -1, 6, -1, -1, 9};
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenRearrangeArrayUsingSwapElement_thenSuccess")
	void givenArray_whenRearrangeArrayUsingSwapElement_thenSuccess() {
		int[] arr = {-1, -1, 6, 1, 9, 3, 2, -1, 4, -1};
		int n = arr.length;
		RearrangeArray.fixArrayUsingSwapElement(arr, n);
		int[] expected = {-1, 1, 2, 3, 4, -1, 6, -1, -1, 9};
		assertArrayEquals(expected, arr);
	}
}