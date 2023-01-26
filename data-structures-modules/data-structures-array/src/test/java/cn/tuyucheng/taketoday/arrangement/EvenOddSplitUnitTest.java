package cn.tuyucheng.taketoday.arrangement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class EvenOddSplitUnitTest {

	@Test
	@DisplayName("givenArray_whenSplitEvenOddElement_thenCorrect")
	void givenArray_whenSplitEvenOddElement_thenCorrect() {
		int[] arr = new int[]{1, 2, 3, 4, 5, 6, 7};
		int n = arr.length;
		int[] expected = {4, 5, 3, 6, 2, 7, 1};
		EvenOddSplit.rearrangeArr(arr, n);
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenSplitEvenOddElementWithOptimization_thenCorrect")
	void givenArray_whenSplitEvenOddElementWithOptimization_thenCorrect() {
		int[] arr = {1, 2, 1, 4, 5, 6, 8, 8};
		int n = arr.length;
		int[] expected = {4, 5, 2, 6, 1, 8, 1, 8};
		EvenOddSplit.rearrangeArr(arr, n);
		assertArrayEquals(expected, arr);
	}
}