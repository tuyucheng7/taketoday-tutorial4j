package app.services;

import app.models.Article;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ArticleServiceImpl implements ArticleService {

	public List<Article> getArticles() {
		return fetchArticles();
	}

	public Article search(String keyword) {
		Article ar = new Article("Article with " + keyword, "tuyucheng", "1250", Instant.now()
			.toString());
		return ar;
	}

	private List<Article> fetchArticles() {
		Article ar1 = new Article("Introduction to ActiveWeb", "tuyucheng", "1650", Instant.now()
			.toString());

		Article ar = new Article("Introduction to Mule", "tuyucheng", "1650", Instant.now()
			.toString());
		List<Article> articles = new ArrayList<Article>();
		articles.add(ar);
		articles.add(ar1);
		return articles;

	}

}
