package cn.tuyucheng.taketoday.springboot.swagger.controller;

import cn.tuyucheng.taketoday.springboot.swagger.model.Article;
import cn.tuyucheng.taketoday.springboot.swagger.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
   public void addArticle(@RequestBody Article article) {
      articleService.addArticle(article);
   }
}