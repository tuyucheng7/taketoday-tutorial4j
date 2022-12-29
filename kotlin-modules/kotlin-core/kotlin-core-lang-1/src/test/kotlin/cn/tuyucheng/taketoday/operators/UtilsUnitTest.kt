package cn.tuyucheng.taketoday.operators

import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class UtilsUnitTest {

	@Test
	fun `We should be able to add an int value to an existing BigInteger using +`() {
		assertEquals(BigInteger.ZERO + 1, BigInteger.ONE)
	}
}