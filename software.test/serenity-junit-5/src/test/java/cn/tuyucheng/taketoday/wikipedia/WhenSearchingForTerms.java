package cn.tuyucheng.taketoday.wikipedia;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.annotations.CastMember;
import net.serenitybdd.screenplay.ensure.Ensure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
class WhenSearchingForTerms {

	@CastMember(name = "Wendy")
	Actor wendy;

	@Test
	void searchBySingleKeyword() {
		wendy.attemptsTo(
			Navigate.toTheHomePage(),
			Search.byKeyword("Everest"),
			Ensure.that(DisplayedArticle.firstHeading()).isEqualTo("Everest")
		);
	}
}