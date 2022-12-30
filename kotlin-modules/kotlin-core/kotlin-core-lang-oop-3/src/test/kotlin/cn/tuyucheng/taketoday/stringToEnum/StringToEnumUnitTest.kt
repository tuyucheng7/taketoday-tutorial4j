package cn.tuyucheng.taketoday.stringToEnum

import cn.tuyucheng.taketoday.stringToEnum.TuyuchengNumber.ONE
import cn.tuyucheng.taketoday.stringToEnum.TuyuchengNumber.TWO
import cn.tuyucheng.taketoday.stringToEnum.TuyuchengNumber.UNKNOWN
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

enum class TuyuchengNumber {
	ONE, TWO, THREE, FOUR, FIVE, UNKNOWN;

	companion object {
		fun byNameIgnoreCaseOrNull(input: String): TuyuchengNumber? {
			return values().firstOrNull { it.name.equals(input, true) }
		}
	}
}

inline fun <reified T : Enum<T>> enumByNameIgnoreCase(input: String, default: T? = null): T? {
	return enumValues<T>().firstOrNull { it.name.equals(input, true) } ?: default
}

class StringToEnumUnitTest {
	@Test
	fun `given a string, when using valueOf(), should get the enum object`() {
		assertEquals(ONE, TuyuchengNumber.valueOf("ONE"))

		assertThrows<IllegalArgumentException> {
			TuyuchengNumber.valueOf("one")
		}
	}

	@Test
	fun `given a string, when using enumValueOf(), should get the enum object`() {
		val theOne = enumValueOf<TuyuchengNumber>("ONE")
		assertEquals(ONE, theOne)

		assertThrows<IllegalArgumentException> {
			enumValueOf<TuyuchengNumber>("one")
		}
	}

	@Test
	fun `given a string, when using values, should get the enum object`() {
		val theOne = TuyuchengNumber.values().firstOrNull { it.name.equals("one", true) }
		assertEquals(ONE, theOne)

		val theTwo = TuyuchengNumber.values().firstOrNull { it.name.equals("TWO", true) }
		assertEquals(TWO, theTwo)

		val theNull = TuyuchengNumber.values().firstOrNull { it.name.equals("whatever", true) }
		assertNull(theNull)

		assertEquals(ONE, TuyuchengNumber.byNameIgnoreCaseOrNull("one"))
		assertNull(TuyuchengNumber.byNameIgnoreCaseOrNull("whatever"))
	}

	@Test
	fun `given a string, when using byNameIgnoreCase, should get the enum object`() {
		val theOne = enumByNameIgnoreCase<TuyuchengNumber>("ONE")
		assertEquals(ONE, theOne)

		val theTwo = enumByNameIgnoreCase<TuyuchengNumber>("two")
		assertEquals(TWO, theTwo)

		val theNull = enumByNameIgnoreCase<TuyuchengNumber>("whatever")
		assertNull(theNull)

		val theDefault = enumByNameIgnoreCase("whatever", UNKNOWN)
		assertEquals(UNKNOWN, theDefault)
	}
}