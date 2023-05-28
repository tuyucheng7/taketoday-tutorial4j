package app.controllers;

import app.services.ArticleService;
import org.javalite.activeweb.AppController;

import javax.inject.Inject;

public class ArticleController extends AppController {

	@Inject
	private ArticleService articleService;

	public void index() {
		view("articles", articleService.getArticles());
	}

	public void search() {

		String keyword = param("key");
		if (null != keyword) {
			assign("article", articleService.search(keyword));
		} else {
			render("/common/error");
		}

	}
}
