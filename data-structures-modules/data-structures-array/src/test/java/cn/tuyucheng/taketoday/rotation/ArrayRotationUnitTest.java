package cn.tuyucheng.taketoday.rotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ArrayRotationUnitTest {

	@Test
	@DisplayName("givenArray_whenRotationUsingTempArray_thenShouldSuccess")
	void givenArray_whenRotationUsingTempArray_thenShouldSuccess() {
		int[] arr = {1, 2, 3, 4, 5, 6, 7};
		int[] expected = {5, 6, 7, 1, 2, 3, 4};
		ArrayRotation.rotateUsingTempArray(arr, arr.length, 4);
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenRotationOneByOne_thenShouldSuccess")
	void givenArray_whenRotationOneByOne_thenShouldSuccess() {
		int[] arr = {1, 2, 3, 4, 5, 6, 7};
		int[] expected = {4, 5, 6, 7, 1, 2, 3};
		ArrayRotation.rotateOneByOne(arr, arr.length, 3);
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenRotationUsingJuggling_thenShouldSuccess")
	void givenArray_whenRotationUsingJuggling_thenShouldSuccess() {
		int[] arr = {1, 2, 3, 4, 5, 6, 7};
		int[] expected = {3, 4, 5, 6, 7, 1, 2};
		ArrayRotation.rotatiteUsingJuggling(arr, arr.length, 2);
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenRotationUsingReversal_thenShouldSuccess")
	void givenArray_whenRotationUsingRevesal_thenShouldSuccess() {
		int[] arr = {1, 2, 3, 4, 5, 6, 7};
		int[] expected = {4, 5, 6, 7, 1, 2, 3};
		ArrayRotation.rotateUsingReversal(arr, 3);
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenRotationUsingRecursiveBlockSwap_thenShouldSuccess")
	void givenArray_whenRotationUsingRucursiveBlockSwap_thenShouldSuccess() {
		int[] arr = {1, 2, 3, 4, 5, 6, 7};
		int[] expected = {4, 5, 6, 7, 1, 2, 3};
		ArrayRotation.rotateUsingBlockSwap(arr, 3, 7);
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenRotationUsingIterativeBlockSwap_thenShouldSuccess")
	void givenArray_whenRotationUsingIterativeBlockSwap_thenShouldSuccess() {
		int[] arr = {1, 2, 3, 4, 5, 6, 7};
		int[] expected = {4, 5, 6, 7, 1, 2, 3};
		ArrayRotation.rotateUsingBlockSwapIterative(arr, 3, 7);
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenRotateArrayByOne_thenShouldSuccess")
	void givenArray_whenRotateArrayByOne_thenShouldSuccess() {
		int[] arr = {1, 2, 3, 4, 5, 6, 7};
		int[] expected = {7, 1, 2, 3, 4, 5, 6};
		ArrayRotation.rotateArrayByOne(arr);
		assertArrayEquals(expected, arr);
	}

	@Test
	@DisplayName("givenArray_whenRotateArrayByOne_thenShouldSuccess")
	void givenArray_whenRotateArrayByOneUsingTwoPointer_thenShouldSuccess() {
		int[] arr = {1, 2, 3, 4, 5, 6, 7};
		int[] expected = {7, 1, 2, 3, 4, 5, 6};
		ArrayRotation.rotateArrayByOneUsingTwoPointer(arr);
		assertArrayEquals(expected, arr);
	}
}