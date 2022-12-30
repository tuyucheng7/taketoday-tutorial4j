package cn.tuyucheng.taketoday.stringconcatenation

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringConcatenationUnitTest {

	@Test
	fun givenTwoStrings_concatenateWithTemplates_thenEquals() {
		val a = "Hello"
		val b = "Tuyucheng"
		val c = "$a $b"

		assertEquals("Hello Tuyucheng", c)
	}

	@Test
	fun givenTwoStrings_concatenateWithPlusOperator_thenEquals() {
		val a = "Hello"
		val b = "Tuyucheng"
		val c = a + " " + b

		assertEquals("Hello Tuyucheng", c)
	}

	@Test
	fun givenTwoStrings_concatenateWithStringBuilder_thenEquals() {
		val a = "Hello"
		val b = "Tuyucheng"

		val builder = StringBuilder()
		builder.append(a).append(" ").append(b)

		val c = builder.toString()

		assertEquals("Hello Tuyucheng", c)
	}

	@Test
	fun givenTwoStrings_concatenateWithPlusMethod_thenEquals() {
		val a = "Hello"
		val b = "Tuyucheng"
		val c = a.plus(" ").plus(b)

		assertEquals("Hello Tuyucheng", c)
	}
}