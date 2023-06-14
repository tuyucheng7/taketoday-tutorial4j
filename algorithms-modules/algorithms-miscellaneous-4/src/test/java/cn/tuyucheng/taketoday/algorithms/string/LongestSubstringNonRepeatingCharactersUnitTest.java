package cn.tuyucheng.taketoday.algorithms.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongestSubstringNonRepeatingCharactersUnitTest {

   @Test
   void givenString_whenGetUniqueCharacterSubstringBruteForceCalled_thenResultFoundAsExpectedUnitTest() {
      Assertions.assertEquals("", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstringBruteForce(""));
      Assertions.assertEquals("A", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstringBruteForce("A"));
      Assertions.assertEquals("ABCDEF", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstringBruteForce("AABCDEF"));
      Assertions.assertEquals("ABCDEF", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstringBruteForce("ABCDEFF"));
      Assertions.assertEquals("NGISAWE", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstringBruteForce("CODINGISAWESOME"));
      Assertions.assertEquals("be coding", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstringBruteForce("always be coding"));
   }

   @Test
   void givenString_whenGetUniqueCharacterSubstringCalled_thenResultFoundAsExpectedUnitTest() {
      Assertions.assertEquals("", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstring(""));
      Assertions.assertEquals("A", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstring("A"));
      Assertions.assertEquals("ABCDEF", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstring("AABCDEF"));
      Assertions.assertEquals("ABCDEF", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstring("ABCDEFF"));
      Assertions.assertEquals("NGISAWE", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstring("CODINGISAWESOME"));
      Assertions.assertEquals("be coding", LongestSubstringNonRepeatingCharacters.getUniqueCharacterSubstring("always be coding"));
   }

}
