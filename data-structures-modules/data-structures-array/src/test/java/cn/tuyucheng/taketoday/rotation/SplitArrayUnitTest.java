package cn.tuyucheng.taketoday.rotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SplitArrayUnitTest {

	@Test
	@DisplayName("givenArray_whenSplitAndAddedTheFirstPartToEnd_thenSuccess")
	void givenArray_whenSplitAndAddedTheFirstPartToEnd_thenSuccess() {
		int[] arr = {12, 10, 5, 6, 52, 36};
		int n = arr.length;
		int position = 2;
		SplitArray.splitArray(arr, n, position);
		int[] expected = {5, 6, 52, 36, 12, 10};
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenSplitAndAddedTheFirstPartToEndUsingTempArray_thenSuccess")
	void givenArray_whenSplitAndAddedTheFirstPartToEndUsingTempArray_thenSuccess() {
		int[] arr = {12, 10, 5, 6, 52, 36};
		int n = arr.length;
		int position = 2;
		SplitArray.splitArrayUsingTempArray(arr, n, position);
		int[] expected = {5, 6, 52, 36, 12, 10};
		assertArrayEquals(expected, arr);
	}
}