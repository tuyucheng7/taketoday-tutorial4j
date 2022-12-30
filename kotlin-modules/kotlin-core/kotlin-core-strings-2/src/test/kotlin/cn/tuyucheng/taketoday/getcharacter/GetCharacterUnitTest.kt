package cn.tuyucheng.taketoday.getcharacter

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetCharacterUnitTest {

	@Test
	fun `should get character from string using indexing`() {
		val string = "Tuyucheng"
		assertEquals('u', string[3])
	}

	@Test
	fun `should get character from string using get`() {
		val string = "Tuyucheng"
		assertEquals('u', string.get(3))
	}

	@Test
	fun `should get first character from string`() {
		val string = "Tuyucheng"
		assertEquals('T', string.first())
	}

	@Test
	fun `should get last character from string`() {
		val string = "Tuyucheng"
		assertEquals('g', string.last())
	}

	@Test
	fun `should get single character from string`() {
		val string = "A"
		assertEquals('A', string.single())
	}

	@Test
	fun `should get single character by converting to array`() {
		val string = "Tuyucheng"
		val toCharArray = string.toCharArray()
		assertEquals('u', toCharArray[3])
	}

	@Test
	fun `should get single character by subsequence`() {
		val string = "Tuyucheng"
		val substring = string.subSequence(3, 4).single()
		assertEquals('u', substring)
	}
}