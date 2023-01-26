package cn.tuyucheng.taketoday.rotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FindMultipleLeftRotationUnitTest {

	@Test
	@DisplayName("givenTwoTimesArray_whenRotateKTimes_thenSuccess")
	void givenTwoTimesArray_whenRotateKTimes_thenSuccess() {
		int[] arr = {1, 3, 5, 7, 9};
		int n = arr.length;
		int[] temp = new int[n * 2];
		FindMultipleLeftRotation.preProcess(arr, n, temp);
		FindMultipleLeftRotation.leftRotate(arr, n, 2, temp);
		FindMultipleLeftRotation.leftRotate(arr, n, 3, temp);
		FindMultipleLeftRotation.leftRotate(arr, n, 4, temp);
	}

	@Test
	@DisplayName("givenTwoTimesArray_whenRotateKTimesOptimization_thenSuccess")
	void givenTwoTimesArray_whenRotateKTimesOptimization_thenSuccess() {
		int[] arr = {1, 3, 5, 7, 9};
		int n = arr.length;
		FindMultipleLeftRotation.spaceOptimizationLefeRotate(arr, n, 2);
		FindMultipleLeftRotation.spaceOptimizationLefeRotate(arr, n, 3);
		FindMultipleLeftRotation.spaceOptimizationLefeRotate(arr, n, 4);
	}

	@Test
	@DisplayName("givenTwoTimesArray_whenRotateKTimesUsingApi_thenSuccess")
	void givenTwoTimesArray_whenRotateKTimesUsingApi_thenSuccess() {
		int[] arr = {1, 3, 5, 7, 9};
		int n = arr.length;
		FindMultipleLeftRotation.spaceOptimizationLefeRotate(arr, n, 1);
		FindMultipleLeftRotation.spaceOptimizationLefeRotate(arr, n, 2);
		FindMultipleLeftRotation.spaceOptimizationLefeRotate(arr, n, 3);
	}
}