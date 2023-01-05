package cn.tuyucheng.taketoday.vertxspring.repository;

import cn.tuyucheng.taketoday.vertxspring.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}