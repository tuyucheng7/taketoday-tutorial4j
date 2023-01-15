package cn.tuyucheng.taketoday.duckduckgo;

import net.serenitybdd.core.steps.UIInteractions;
import net.thucydides.core.annotations.Step;

public class SearchActions extends UIInteractions {

	@Step("Search for '{0}'")
	public void byKeyword(String keyword) {
		$("#search_form_input_homepage").sendKeys(keyword);
		$(".search__button").click();
	}
}