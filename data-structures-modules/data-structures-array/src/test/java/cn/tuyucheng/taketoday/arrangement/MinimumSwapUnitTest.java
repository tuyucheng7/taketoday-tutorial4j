package cn.tuyucheng.taketoday.arrangement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MinimumSwapUnitTest {

	@Test
	@DisplayName("givenArray_whenFindMinimumSwap_thenCorrect")
	void givenArray_whenFindMinimumSwap_thenCorrect() {
		int[] arr = {2, 7, 9, 5, 8, 7, 4};
		int minSwapTime = MinimumSwap.minSwap(arr, 5);
		assertEquals(2, minSwapTime);
	}
}