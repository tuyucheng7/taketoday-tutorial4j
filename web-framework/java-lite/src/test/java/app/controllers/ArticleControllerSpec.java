package app.controllers;

import app.services.ArticleServiceModule;
import com.google.inject.Guice;
import org.javalite.activeweb.ControllerSpec;
import org.junit.Before;
import org.junit.Test;

public class ArticleControllerSpec extends ControllerSpec {

	@Before
	public void before() {
		setInjector(Guice.createInjector(new ArticleServiceModule()));
	}

	@Test
	public void whenReturnedArticlesThenCorrect() {
		request().get("index");
		a(responseContent()).shouldContain("<td>Introduction to Mule</td>");
	}

	@Test
	public void givenKeywordWhenFoundArticleThenCorrect() {
		request().param("key", "Java")
			.get("search");
		a(responseContent()).shouldContain("<td>Article with Java</td>");
	}

}
