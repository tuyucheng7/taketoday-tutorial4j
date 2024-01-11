package cn.tuyucheng.taketoday.mybatis.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ArticleMapperCommonUnitTest {

   @Autowired
   ArticleMapper articleMapper;

   @Test
   void whenRecordsInDatabase_shouldReturnArticleWithGivenId() {
      Article article = articleMapper.getArticle(1L);

      assertThat(article).isNotNull();
      assertThat(article.getId()).isEqualTo(1L);
      assertThat(article.getAuthor()).isEqualTo("Tuyucheng");
      assertThat(article.getTitle()).isEqualTo("Working with MyBatis in Spring");
   }
}