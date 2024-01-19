package cn.tuyucheng.taketoday.morse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MorseTranslatorUnitTest {

   @ParameterizedTest
   @ValueSource(strings = {"MORSE CODE!", "morse code!", "mOrSe cOdE!"})
   void givenAValidEnglishWordWhateverTheCapitalization_whenEnglishToMorse_thenTranslatedToMorse(String english) {
      assertEquals("-- --- .-. ... . / -.-. --- -.. . -.-.-----.", MorseTranslator.englishToMorse(english));
   }

   @Test
   void givenAnEnglishWordWithAnIllegalCharacter_whenEnglishToMorse_thenThrows() {
      String english = "~This sentence starts with an illegal character";
      assertThrows(IllegalArgumentException.class, () -> MorseTranslator.englishToMorse(english));
   }

   @Test
   void givenNull_whenEnglishToMorse_thenNull() {
      assertNull(MorseTranslator.englishToMorse(null));
   }

   @Test
   void givenEmptyString_whenEnglishToMorse_thenEmptyArray() {
      assertEquals("", MorseTranslator.englishToMorse(""));
   }

   @Test
   void givenAValidMorseWord_whenMorseToEnglish_thenTranslatedToUpperCaseEnglish() {
      assertEquals("MORSE CODE!", MorseTranslator.morseToEnglish("-- --- .-. ... . / -.-. --- -.. . -.-.-----."));
   }

   @Test
   void givenAMorseWordWithAnIllegalCharacter_whenMorseToEnglish_thenThrows() {
      assertThrows(IllegalArgumentException.class, () -> MorseTranslator.morseToEnglish(".!!!!!!!"));
   }

   @Test
   void givenNull_whenMorseToEnglish_thenNull() {
      assertNull(MorseTranslator.morseToEnglish(null));
   }

   @Test
   void givenEmptyArray_whenMorseToEnglish_thenEmptyString() {
      assertEquals("", MorseTranslator.morseToEnglish(""));
   }
}