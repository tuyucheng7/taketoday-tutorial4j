package cn.tuyucheng.taketoday.wikipedia;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.questions.Text;

/**
 * Represents information appearing on an article displayed on Wikipedia
 */
public class DisplayedArticle {

	public static Question<String> firstHeading() {
		return Text.of("#firstHeading");
	}
}