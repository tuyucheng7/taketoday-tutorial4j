package cn.tuyucheng.taketoday.arrangement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MoveZeroUnitTest {

	@Test
	@DisplayName("givenArray_whenMoveAllZeroToEnd_thenShouldSuccess")
	void givenArray_whenMoveAllZeroToEnd_thenShouldSuccess() {
		int[] arr = {1, 9, 8, 4, 0, 0, 2, 7, 0, 6, 0, 9};
		int n = arr.length;
		int[] expected = {1, 9, 8, 4, 2, 7, 6, 9, 0, 0, 0, 0};
		MoveZero.pushZeroToEnd(arr, n);
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenMoveAllZeroToEndUsingPartition_thenShouldSuccess")
	void givenArray_whenMoveAllZeroToEndUsingPartition_thenShouldSuccess() {
		int[] arr = {1, 9, 8, 4, 0, 0, 2, 7, 0, 6, 0, 9};
		int[] expected = {1, 9, 8, 4, 2, 7, 6, 9, 0, 0, 0, 0};
		MoveZero.pushZeroToEndUsingPartition(arr);
		assertArrayEquals(expected, arr);
	}
}