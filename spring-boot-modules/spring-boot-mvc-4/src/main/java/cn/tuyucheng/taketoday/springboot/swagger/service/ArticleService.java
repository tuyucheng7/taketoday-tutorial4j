package cn.tuyucheng.taketoday.springboot.swagger.service;

import cn.tuyucheng.taketoday.springboot.swagger.model.Article;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {

    private List<Article> articles = new ArrayList<>();

    public List<Article> getAllArticles() {
        return articles;
    }

    public void addArticle(Article article) {
        article.setId(articles.size() + 1);
        articles.add(article);
    }
}