package cn.tuyucheng.taketoday.base64

import org.junit.jupiter.api.Test
import java.util.Base64
import kotlin.test.assertEquals
import org.apache.commons.codec.binary.Base64 as ApacheBase64

class Base64UnitTest {
	@Test
	fun `check Base64 encoding of string using Base64 utility`() {
		val originalString = "Tuyucheng"
		val encodedString: String = Base64.getEncoder().encodeToString(originalString.toByteArray())
		assertEquals("VHV5dWNoZW5n", encodedString)
	}

	@Test
	fun `check Base64 decoding of string using Base64 utility`() {
		val encodedString = "VHV5dWNoZW5n"
		val decodedString: String = String(Base64.getDecoder().decode(encodedString))
		assertEquals("Tuyucheng", decodedString)
	}

	@Test
	fun `check Base64 encoding of string using ApacheBase64 utility`() {
		val originalString = "Tuyucheng"
		val base64: ApacheBase64 = ApacheBase64()
		val encodedStr = String(base64.encode(originalString.toByteArray()))
		assertEquals("VHV5dWNoZW5n", encodedStr)
	}

	@Test
	fun `check Base64 decoding of string using ApacheBase64 utility`() {
		val encodedString = "VHV5dWNoZW5n"
		val base64: ApacheBase64 = ApacheBase64()
		val decodedString: String = String(base64.decode(encodedString))
		assertEquals("Tuyucheng", decodedString)
	}
}