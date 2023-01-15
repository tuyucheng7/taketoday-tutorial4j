package cn.tuyucheng.taketoday.hexformat;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringFormattingUnitTest {

	private final HexFormat hexFormat = HexFormat.of().withPrefix("[").withSuffix("]").withDelimiter(", ");

	@Test
	void givenInitialisedHexFormatWithFormattedStringOptions_whenByteArrayIsPassed_thenHexStringRepresentationFormattedCorrectlyIsReturned() {
		assertEquals("[48], [0c], [11]", hexFormat.formatHex(new byte[]{72, 12, 17}));
	}
}