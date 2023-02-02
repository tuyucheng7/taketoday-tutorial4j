package cn.tuyucheng.taketoday.hexformat;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UppercaseLowercaseOutputUnitTest {

	@Test
	void givenInitialisedHexFormat_whenByteArrayIsPassed_thenLowerCaseHexStringRepresentationIsReturned() {
		HexFormat hexFormat = HexFormat.of();
		String bytesAsString = hexFormat.formatHex(new byte[]{-85, -51, -17, 1, 35, 69, 103, -119});
		assertTrue(isLowerCase(bytesAsString));
	}

	@Test
	void givenInitialisedHexFormatWithUpperCaseOption_whenByteArrayIsPassed_thenLowerCaseHexStringRepresentationIsReturned() {
		HexFormat hexFormat = HexFormat.of().withUpperCase();
		String bytesAsString = hexFormat.formatHex(new byte[]{-85, -51, -17, 1, 35, 69, 103, -119});
		assertTrue(isUpperCase(bytesAsString));
	}

	private boolean isLowerCase(String str) {
		char[] charArray = str.toCharArray();
		for (char c : charArray) {
			if (Character.isUpperCase(c))
				return false;
		}
		return true;
	}

	private boolean isUpperCase(String str) {
		char[] charArray = str.toCharArray();
		for (char c : charArray) {
			if (Character.isLowerCase(c))
				return false;
		}
		return true;
	}
}