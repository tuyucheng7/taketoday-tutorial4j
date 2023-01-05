package cn.tuyucheng.taketoday.vertxspring.service;

import cn.tuyucheng.taketoday.vertxspring.entity.Article;
import cn.tuyucheng.taketoday.vertxspring.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

	@Autowired
	private ArticleRepository articleRepository;

	public List<Article> getAllArticle() {
		return articleRepository.findAll();
	}
}