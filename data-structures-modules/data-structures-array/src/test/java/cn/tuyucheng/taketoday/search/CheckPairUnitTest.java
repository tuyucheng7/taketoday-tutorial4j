package cn.tuyucheng.taketoday.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckPairUnitTest {

	@Test
	@DisplayName("givenArray_whenFindPairSumEqualsToInput_thenTrue")
	void givenArray_whenFindPairSumEqualsToInput_thenTrue() {
		int[] A = {0, -1, 2, -3, 1};
		int x = -2;
		int size = A.length;
		boolean isExists = CheckPair.checkPair(A, size, x);
		assertTrue(isExists);
	}

	@Test
	@DisplayName("givenArray_whenNotExistsPairSumEqualsToInput_thenFalse")
	void givenArray_whenNotExistsPairSumEqualsToInput_thenFalse() {
		int[] A = {0, -1, 2, -3, 1};
		int x = 5;
		int size = A.length;
		boolean isExists = CheckPair.checkPair(A, size, x);
		assertFalse(isExists);
	}

	@Test
	@DisplayName("givenArray_whenFindPairSumEqualsToInputUsingTwoPointer_thenTrue")
	void givenArray_whenFindPairSumEqualsToInputUsingTwoPointer_thenTrue() {
		int[] A = {1, 4, 45, 6, 10, -8};
		int x = 16;
		int size = A.length;
		boolean isExists = CheckPair.checkPair(A, size, x);
		assertTrue(isExists);
	}

	@Test
	@DisplayName("givenArray_whenNotExistsPairSumEqualsToInputUsingTwoPointer_thenFalse")
	void givenArray_whenNotExistsPairSumEqualsToInputUsingTwoPointer_thenFalse() {
		int[] A = {1, 4, 45, 6, 10, -8};
		int x = 13;
		int size = A.length;
		boolean isExists = CheckPair.checkPair(A, size, x);
		assertFalse(isExists);
	}

	@Test
	@DisplayName("givenArray_whenFindPairSumEqualsToInputUsingHashing_thenTrue")
	void givenArray_whenFindPairSumEqualsToInputUsingHashing_thenTrue() {
		int[] A = {1, 4, 45, 6, 10, -8};
		int x = 16;
		int size = A.length;
		boolean isExists = CheckPair.checkPair(A, size, x);
		assertTrue(isExists);
	}

	@Test
	@DisplayName("givenArray_whenNotExistsPairSumEqualsToInputUsingHashing_thenFalse")
	void givenArray_whenNotExistsPairSumEqualsToInputUsingHashing_thenFalse() {
		int[] A = {1, 4, 45, 6, 10, -8};
		int x = 13;
		int size = A.length;
		boolean isExists = CheckPair.checkPair(A, size, x);
		assertFalse(isExists);
	}
}