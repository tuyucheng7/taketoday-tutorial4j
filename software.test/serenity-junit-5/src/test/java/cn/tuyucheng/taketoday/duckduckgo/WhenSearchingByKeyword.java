package cn.tuyucheng.taketoday.duckduckgo;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.thucydides.core.annotations.Managed;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
class WhenSearchingByKeyword {

	@Managed(driver = "chrome", options = "headless")
	WebDriver driver;

	NavigateActions navigate;
	SearchActions search;
	SearchResultSidebar searchResultSidebar;

	@Test
	void theKeywordShouldAppearInTheResultsSidebar() {
		navigate.toTheDuckDuckGoSearchPage();
		search.byKeyword("Cucumber");

		Serenity.reportThat("The keyword should appear in the sidebar heading",
			() -> assertThat(searchResultSidebar.heading()).isEqualTo("Cucumber")
		);
	}
}