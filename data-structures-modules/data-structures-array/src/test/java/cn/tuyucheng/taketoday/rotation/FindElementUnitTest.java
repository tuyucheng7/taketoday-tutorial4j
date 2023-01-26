package cn.tuyucheng.taketoday.rotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindElementUnitTest {

	@Test
	@DisplayName("givenArray_whenFindElement_thenCorrect")
	void givenArray_whenFindElement_thenCorrect() {
		int[] arr = {1, 2, 3, 4, 5};
		int rotations = 2;
		int[][] ranges = {{0, 2}, {0, 3}};
		int findElement = FindElement.findElement(arr, ranges, rotations, 1);
		assertEquals(3, findElement);
	}
}