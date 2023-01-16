package cn.tuyucheng.taketoday.algorithms.string;

import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.algorithms.string.LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstring;
import static cn.tuyucheng.taketoday.algorithms.string.LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstringBruteForce;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LongestSubstringNonRepeatingCharactersUnitTest {

	@Test
	void givenString_whenGetUniqueCharacterSubstringBruteForceCalled_thenResultFoundAsExpectedUnitTest() {
		assertEquals("", getUniqueCharacterSubstringBruteForce(""));
		assertEquals("A", getUniqueCharacterSubstringBruteForce("A"));
		assertEquals("ABCDEF", getUniqueCharacterSubstringBruteForce("AABCDEF"));
		assertEquals("ABCDEF", getUniqueCharacterSubstringBruteForce("ABCDEFF"));
		assertEquals("NGISAWE", getUniqueCharacterSubstringBruteForce("CODINGISAWESOME"));
		assertEquals("be coding", getUniqueCharacterSubstringBruteForce("always be coding"));
	}

	@Test
	void givenString_whenGetUniqueCharacterSubstringCalled_thenResultFoundAsExpectedUnitTest() {
		assertEquals("", getUniqueCharacterSubstring(""));
		assertEquals("A", getUniqueCharacterSubstring("A"));
		assertEquals("ABCDEF", getUniqueCharacterSubstring("AABCDEF"));
		assertEquals("ABCDEF", getUniqueCharacterSubstring("ABCDEFF"));
		assertEquals("NGISAWE", getUniqueCharacterSubstring("CODINGISAWESOME"));
		assertEquals("be coding", getUniqueCharacterSubstring("always be coding"));
	}

}
