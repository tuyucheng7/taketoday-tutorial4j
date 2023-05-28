package app.services;

import app.models.Article;

import java.util.List;

public interface ArticleService {

	List<Article> getArticles();

	Article search(String keyword);

}
