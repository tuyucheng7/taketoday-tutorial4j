package cn.tuyucheng.taketoday.web.controller.optionalpathvars;

import cn.tuyucheng.taketoday.model.Article;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.tuyucheng.taketoday.model.Article.DEFAULT_ARTICLE;

@RestController
public class ArticleViewerController {

    @RequestMapping(value = {"/article", "/article/{id}"})
    public Article getArticle(@PathVariable(name = "id") Integer articleId) {

        if (articleId != null) {
            return new Article(articleId);
        } else {
            return DEFAULT_ARTICLE;
        }

    }
}