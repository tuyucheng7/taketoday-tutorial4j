package cn.tuyucheng.taketoday.wikipedia;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.ui.InputField;
import org.openqa.selenium.Keys;

/**
 * Wikipedia search actions
 */
public class Search {
	public static Performable byKeyword(String keyword) {
		return Task.where("{0} searches for articles containing they word " + keyword,
			Enter.theValue(keyword).into(InputField.withNameOrId("search")).thenHit(Keys.ENTER)
		);
	}
}
