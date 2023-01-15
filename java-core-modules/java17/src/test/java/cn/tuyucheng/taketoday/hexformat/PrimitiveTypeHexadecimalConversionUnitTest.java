package cn.tuyucheng.taketoday.hexformat;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrimitiveTypeHexadecimalConversionUnitTest {

	private final HexFormat hexFormat = HexFormat.of();

	@Test
	void givenInitialisedHexFormat_whenPrimitiveByteIsPassed_thenHexStringRepresentationIsReturned() {
		String fromByte = hexFormat.toHexDigits((byte) 64);
		assertEquals("40", fromByte);
	}

	@Test
	void givenInitialisedHexFormat_whenPrimitiveLongIsPassed_thenHexStringRepresentationIsReturned() {
		String fromLong = hexFormat.toHexDigits(1234_5678_9012_3456L);
		assertEquals("000462d53c8abac0", fromLong);
	}
}