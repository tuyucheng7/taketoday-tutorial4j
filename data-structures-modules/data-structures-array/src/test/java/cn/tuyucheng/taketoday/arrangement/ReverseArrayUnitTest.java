package cn.tuyucheng.taketoday.arrangement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ReverseArrayUnitTest {

	@Test
	@DisplayName("givenArray_whenReverseIt_thenShuoldSuccess")
	void givenArray_whenReverseIt_thenShuoldSuccess() {
		int[] arr = {1, 2, 3, 4, 5};
		int start = 0;
		int end = arr.length - 1;
		int[] expected = {5, 4, 3, 2, 1};
		ReverseArray.reverseArray(arr, start, end);
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenReverseItUsingRecursive_thenShuoldSuccess")
	void givenArray_whenReverseItUsingRecursive_thenShuoldSuccess() {
		int[] arr = {1, 2, 3, 4, 5};
		int start = 0;
		int end = arr.length - 1;
		int[] expected = {5, 4, 3, 2, 1};
		ReverseArray.reverseArrayRecursive(arr, start, end);
		assertArrayEquals(expected, arr);
	}
}