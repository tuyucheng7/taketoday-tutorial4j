package cn.tuyucheng.taketoday.dynamicproperties;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}