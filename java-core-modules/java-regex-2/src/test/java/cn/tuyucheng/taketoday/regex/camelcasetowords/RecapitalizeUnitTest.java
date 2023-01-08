package cn.tuyucheng.taketoday.regex.camelcasetowords;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class RecapitalizeUnitTest {

	@Test
	void givenWords_thenCanComposeSentence() {
		Assertions.assertThat(Recapitalize.sentenceCase(Arrays.asList("these", "Words", "Form", "A", "Sentence")))
			.isEqualTo("These words form a sentence.");
	}

	@Test
	void givenNonStopWords_thenTitleIsComposed() {
		Assertions.assertThat(Recapitalize.capitalizeMyTitle(Arrays.asList("title", "words", "capitalize")))
			.isEqualTo("Title Words Capitalize");
	}

	@Test
	void givenStopWords_thenTitleHasThemInLowerCase() {
		Assertions.assertThat(Recapitalize.capitalizeMyTitle(Arrays.asList("this", "is", "A", "title", "with", "a", "stop", "word", "or", "two")))
			.isEqualTo("This Is a Title With a Stop Word or Two");
	}

	@Test
	void givenStopWordIsFirstWord_thenTitleHasItCapitalized() {
		Assertions.assertThat(Recapitalize.capitalizeMyTitle(Arrays.asList("a", "stop", "word", "first")))
			.isEqualTo("A Stop Word First");
	}
}