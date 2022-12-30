package cn.tuyucheng.taketoday.remove_character

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RemoveCharacterUnitTest {

	@Test
	fun `should remove character from string using replace`() {
		val string = "Tu.yucheng"
		assertEquals("Tuyucheng", string.replace(".", ""))
	}

	@Test
	fun `should remove character from string using filter`() {
		val string = "Tu.yucheng"
		assertEquals("Tuyucheng", string.filterNot { it == '.' })
	}

	@Test
	fun `should remove character from string using deleteAt`() {
		val string = "Tu.yucheng"
		val stringBuilder = StringBuilder(string)
		assertEquals("Tuyucheng", stringBuilder.deleteAt(2).toString())
	}

	@Test
	fun `should remove character by using removeRange`() {
		val string = "Tu.yucheng"
		assertEquals("Tuyucheng", string.removeRange(2, 3))
	}

	@Test
	fun `should remove last character from string`() {
		val string = "Tuyucheng"
		assertEquals("Tuyuchen", string.removeSuffix("g"))
		assertEquals("Tuyucheng", string.removePrefix("Z"))
	}

	@Test
	fun `should remove first character from string`() {
		val string = "Tuyucheng"
		assertEquals("uyucheng", string.removePrefix("T"))
		assertEquals("Tuyucheng", string.removePrefix("Z"))
	}
}