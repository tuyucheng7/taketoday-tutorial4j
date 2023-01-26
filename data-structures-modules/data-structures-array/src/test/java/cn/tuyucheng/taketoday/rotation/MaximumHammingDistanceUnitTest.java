package cn.tuyucheng.taketoday.rotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaximumHammingDistanceUnitTest {

	@Test
	@DisplayName("givenArray_whenFindMaximumHammingDistance_thenReturnCorrect")
	void givenArray_whenFindMaximumHammingDistance_thenReturnCorrect() {
		int[] arr = {2, 4, 6, 8};
		int n = arr.length;
		int maxHamming = MaximumHammingDistance.maxHamming(arr, n);
		assertEquals(4, maxHamming);
	}

	@Test
	@DisplayName("givenArray_whenFindMaximumHammingDistanceWithConstantSpace_thenReturnCorrect")
	void givenArray_whenFindMaximumHammingDistanceWithConstantSpace_thenReturnCorrect() {
		int[] arr = {1, 4, 1};
		int n = arr.length;
		int maxHamming = MaximumHammingDistance.maxHamming(arr, n);
		assertEquals(2, maxHamming);
	}
}