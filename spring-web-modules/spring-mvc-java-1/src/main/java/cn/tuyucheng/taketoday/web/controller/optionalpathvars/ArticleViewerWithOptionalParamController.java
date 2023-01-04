package cn.tuyucheng.taketoday.web.controller.optionalpathvars;

import cn.tuyucheng.taketoday.model.Article;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static cn.tuyucheng.taketoday.model.Article.DEFAULT_ARTICLE;

;

@RestController
@RequestMapping("/optionalParam")
public class ArticleViewerWithOptionalParamController {

    @RequestMapping(value = {"/article", "/article/{id}"})
    public Article getArticle(@PathVariable(name = "id") Optional<Integer> optionalArticleId) {

        if (optionalArticleId.isPresent()) {
            Integer articleId = optionalArticleId.get();
            return new Article(articleId);
        } else {
            return DEFAULT_ARTICLE;
        }
    }
}