package cn.tuyucheng.taketoday.longtoint

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LongToIntUnitTest {

	@Test
	fun `when toInt called on long result is an integer`(): Unit {
		val longValue = 100L
		val intValue = longValue.toInt()
		assertThat(100).isEqualTo(intValue)
	}
}