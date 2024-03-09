package cn.tuyucheng.taketoday.springmodulith.events.externalization;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Tuyucheng {

   private final ApplicationEventPublisher applicationEvents;
   private final ArticleRepository articleRepository;

   public Tuyucheng(ApplicationEventPublisher applicationEvents, ArticleRepository articleRepository) {
      this.applicationEvents = applicationEvents;
      this.articleRepository = articleRepository;
   }

   @Transactional
   public void createArticle(Article article) {
      // ... business logic
      validateArticle(article);
      article = addArticleTags(article);
      article = articleRepository.save(article);

      applicationEvents.publishEvent(new ArticlePublishedEvent(article.slug(), article.title()));
   }

   private Article addArticleTags(Article article) {
      return article;
   }

   private void validateArticle(Article article) {
   }
}