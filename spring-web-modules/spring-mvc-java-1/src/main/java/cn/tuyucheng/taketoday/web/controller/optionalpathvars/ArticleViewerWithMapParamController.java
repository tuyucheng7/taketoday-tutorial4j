package cn.tuyucheng.taketoday.web.controller.optionalpathvars;

import cn.tuyucheng.taketoday.model.Article;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static cn.tuyucheng.taketoday.model.Article.DEFAULT_ARTICLE;

@RestController
@RequestMapping(value = "/mapParam")
public class ArticleViewerWithMapParamController {

    @RequestMapping(value = {"/article", "/article/{id}"})
    public Article getArticle(@PathVariable Map<String, String> pathVarsMap) {

        String articleId = pathVarsMap.get("id");

        if (articleId != null) {
            return new Article(Integer.valueOf(articleId));
        } else {
            return DEFAULT_ARTICLE;
        }
    }
}