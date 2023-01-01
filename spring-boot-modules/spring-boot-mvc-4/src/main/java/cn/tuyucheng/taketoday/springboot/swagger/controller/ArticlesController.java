package cn.tuyucheng.taketoday.springboot.swagger.controller;

import cn.tuyucheng.taketoday.springboot.swagger.model.Article;
import cn.tuyucheng.taketoday.springboot.swagger.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("")
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    @PostMapping("")
    public void addArticle(@ModelAttribute Article article) {
        articleService.addArticle(article);
    }
}