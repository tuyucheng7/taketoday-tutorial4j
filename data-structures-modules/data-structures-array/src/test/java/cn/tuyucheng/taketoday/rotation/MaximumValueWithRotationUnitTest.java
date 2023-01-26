package cn.tuyucheng.taketoday.rotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaximumValueWithRotationUnitTest {

	@Test
	@DisplayName("givenRotateArray_whenCalculateMaximum_thenCorrect")
	void givenRotateArray_whenCalculateMaximum_thenCorrect() {
		int[] arr = new int[]{10, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		int maximum = MaximumValueWithRotation.maxSum(arr);
		assertEquals(330, maximum);
	}

	@Test
	@DisplayName("givenRotateArray_whenCalculateMaximumUsingNativeMethod_thenCorrect")
	void givenRotateArray_whenCalculateMaximumUsingNativeMethod_thenCorrect() {
		int[] arr = {8, 3, 1, 2};
		int n = arr.length;
		int maximum = MaximumValueWithRotation.maxSumNativeMethod(arr, n);
		assertEquals(29, maximum);
	}

	@Test
	@DisplayName("givenRotateArray_whenCalculateMaximumUsingFormula_thenCorrect")
	void givenRotateArray_whenCalculateMaximumUsingFormula_thenCorrect() {
		int[] arr = {8, 3, 1, 2};
		int n = arr.length;
		int maximum = MaximumValueWithRotation.maxSumUsingFormula(arr);
		assertEquals(29, maximum);
	}
}