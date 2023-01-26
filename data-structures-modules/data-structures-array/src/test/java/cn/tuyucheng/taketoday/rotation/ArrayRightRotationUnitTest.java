package cn.tuyucheng.taketoday.rotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ArrayRightRotationUnitTest {

	@Test
	@DisplayName("givenArray_whenRightRotateThreeTimes_thenCorrect")
	void givenArray_whenRightRotateThreeTimes_thenCorrect() {
		int[] arr = {1, 2, 3, 4, 5};
		int n = arr.length;
		ArrayRightRotation.rightRotate(arr, n, 3);
		int[] expected = {3, 4, 5, 1, 2};
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenNotRightRotate_thenCorrect")
	void givenArray_whenNotRightRotate_thenCorrect() {
		int[] arr = {1, 2, 3, 4, 5};
		int n = arr.length;
		ArrayRightRotation.rightRotate(arr, n, 0);
		int[] expected = {1, 2, 3, 4, 5};
		assertArrayEquals(expected, arr);
	}
}