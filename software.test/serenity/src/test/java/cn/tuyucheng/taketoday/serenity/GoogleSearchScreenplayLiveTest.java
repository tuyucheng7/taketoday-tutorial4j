package cn.tuyucheng.taketoday.serenity;

import cn.tuyucheng.taketoday.serenity.screenplay.GoogleSearchResults;
import cn.tuyucheng.taketoday.serenity.screenplay.SearchForKeyword;
import cn.tuyucheng.taketoday.serenity.screenplay.StartWith;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.GivenWhenThen;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Managed;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.givenThat;
import static net.serenitybdd.screenplay.GivenWhenThen.then;
import static net.serenitybdd.screenplay.GivenWhenThen.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;

@RunWith(SerenityRunner.class)
public class GoogleSearchScreenplayLiveTest {

   @Managed(driver = "chrome")
   private WebDriver browser;

   private Actor kitty = Actor.named("kitty");

   @Before
   public void setup() {
      kitty.can(BrowseTheWeb.with(browser));
   }

   @Test
   public void whenGoogleBaeldungThenShouldSeeEugen() {
      givenThat(kitty).wasAbleTo(StartWith.googleSearchPage());

      when(kitty).attemptsTo(SearchForKeyword.of("baeldung"));

      then(kitty).should(GivenWhenThen.seeThat(GoogleSearchResults.displayed(), hasItem(containsString("Eugen (Baeldung)"))));
   }

}
