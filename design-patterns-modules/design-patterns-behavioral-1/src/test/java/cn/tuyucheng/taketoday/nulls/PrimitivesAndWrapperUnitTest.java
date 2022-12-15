package cn.tuyucheng.taketoday.nulls;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrimitivesAndWrapperUnitTest {

	@Test
	void givenBothArgsNonNull_whenCallingWrapperSum_thenReturnSum() {
		Integer sum = PrimitivesAndWrapper.wrapperSum(0, 0);

		assertEquals(0, sum.intValue());
	}

	@Test()
	void givenOneArgIsNull_whenCallingWrapperSum_thenThrowNullPointerException() {
		assertThrows(NullPointerException.class, () -> PrimitivesAndWrapper.wrapperSum(null, 2));
	}

	@Test()
	void givenBothArgsNull_whenCallingWrapperSum_thenThrowNullPointerException() {
		assertThrows(NullPointerException.class, () -> PrimitivesAndWrapper.wrapperSum(null, null));
	}

	@Test()
	void givenOneArgNull_whenCallingGoodSum_thenThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> PrimitivesAndWrapper.goodSum(null, 2));
	}
}