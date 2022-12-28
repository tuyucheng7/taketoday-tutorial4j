package cn.tuyucheng.taketoday.hex

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.security.MessageDigest

class HexUnitTest {

	@Test
	@ExperimentalUnsignedTypes
	fun `should convert byte arrays to hex values as expected`() {
		val md5 = MessageDigest.getInstance("md5")
		md5.update("tuyucheng".toByteArray())

		val digest: ByteArray = md5.digest()
		// the hex value of MD5 digest for "tuyucheng" is "0e80037b831803baadee4249e398328b"
		assertEquals("0e80037b831803baadee4249e398328b", digest.toHex())
		assertEquals("0e80037b831803baadee4249e398328b", digest.toHex2())
		assertEquals("0e80037b831803baadee4249e398328b", digest.toHex3())
		assertEquals("0e80037b831803baadee4249e398328b", digest.toHex4())
	}
}

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

@ExperimentalUnsignedTypes
fun ByteArray.toHex2(): String = asUByteArray().joinToString("") { it.toString(radix = 16).padStart(2, '0') }

fun ByteArray.toHex3(): String = joinToString("") {
	java.lang.Byte.toUnsignedInt(it).toString(radix = 16).padStart(2, '0')
}

val hexChars = "0123456789abcdef".toCharArray()

fun ByteArray.toHex4(): String {
	val hex = CharArray(2 * this.size)
	this.forEachIndexed { i, byte ->
		val unsigned = 0xff and byte.toInt()
		hex[2 * i] = hexChars[unsigned / 16]
		hex[2 * i + 1] = hexChars[unsigned % 16]
	}

	return hex.joinToString("")
}