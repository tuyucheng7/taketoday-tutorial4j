package cn.tuyucheng.taketoday.arrangement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class PositiveNegativeSplitUnitTest {

	@Test
	@DisplayName("givenArray_whenRearrangePositiveAndNegativeNumbers_thenCorrect")
	void givenArray_whenRearrangePositiveAndNegativeNumbers_thenCorrect() {
		int[] arr = {-1, 2, -3, 4, 5, 6, -7, 8, 9};
		int n = arr.length;
		int[] expected = {4, -3, 5, -1, 6, -7, 2, 8, 9};
		PositiveNegativeSplit.rearrange(arr, n);
		assertArrayEquals(expected, arr);
	}
}