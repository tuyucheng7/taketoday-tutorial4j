package cn.tuyucheng.taketoday.regex.camelcasetowords;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CamelCaseToWordsUnitTest {

	@Test
	void givenPlainStringWithNonLetters_thenFindsWords() {
		Assertions.assertThat(CamelCaseToWords.findWordsInMixedCase("some words"))
			.containsExactly("some", "words");
	}

	@Test
	void givenWordsInCamelCase_thenFindsWords() {
		Assertions.assertThat(CamelCaseToWords.findWordsInMixedCase("thisIsCamelCaseText"))
			.containsExactly("this", "Is", "Camel", "Case", "Text");
	}

	@Test
	void givenWordsInTitleCase_thenFindsWords() {
		Assertions.assertThat(CamelCaseToWords.findWordsInMixedCase("ThisIsTitleCaseText"))
			.containsExactly("This", "Is", "Title", "Case", "Text");
	}

	@Test
	void givenWordsAcrossMultipleTexts_thenFindsWords() {
		Assertions.assertThat(CamelCaseToWords.findWordsInMixedCase("ThisIsTitleCaseText --- andSoIsThis"))
			.containsExactly("This", "Is", "Title", "Case", "Text", "and", "So", "Is", "This");
	}

	@Test
	void givenCamelCaseHasASingleLetterWord_thenItCanBeSplit() {
		Assertions.assertThat(CamelCaseToWords.findWordsInMixedCase("thisHasASingleLetterWord"))
			.containsExactly("this", "Has", "A", "Single", "Letter", "Word");
	}
}