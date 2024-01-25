package cn.tuyucheng.taketoday.springmodulith.events.externalization;

import cn.tuyucheng.taketoday.springmodulith.Application;
import cn.tuyucheng.taketoday.springmodulith.events.externalization.listener.TestKafkaListenerConfig;
import cn.tuyucheng.taketoday.springmodulith.events.externalization.listener.TestListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest(classes = {Application.class, TestKafkaListenerConfig.class})
class EventsExternalizationLiveTest {

   @Autowired
   private Tuyucheng tuyucheng;
   @Autowired
   private TestListener listener;
   @Autowired
   private ArticleRepository repository;

   @Container
   static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

   @DynamicPropertySource
   static void dynamicProperties(DynamicPropertyRegistry registry) {
      registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
   }

   static {
      Awaitility.setDefaultTimeout(ofSeconds(3));
      Awaitility.setDefaultPollDelay(ofMillis(100));
   }

   @BeforeEach
   void beforeEach() {
      listener.reset();
      repository.deleteAll();
   }

   @Test
   void whenArticleIsSavedToDB_thenItIsAlsoPublishedToKafka() {
      var article = new Article("introduction-to-spring-boot", "Introduction to Spring Boot", "John Doe", "<p> Spring Boot is [...] </p>");

      tuyucheng.createArticle(article);

      await().untilAsserted(() ->
            assertThat(listener.getEvents())
                  .hasSize(1)
                  .first().asString()
                  .contains("\"slug\":\"introduction-to-spring-boot\"")
                  .contains("\"title\":\"Introduction to Spring Boot\""));

      assertThat(repository.findAll())
            .hasSize(1)
            .first()
            .extracting(Article::slug, Article::title)
            .containsExactly("introduction-to-spring-boot", "Introduction to Spring Boot");
   }

   @Test
   void whenPublishingMessageFails_thenArticleIsStillSavedToDB() {
      var article = new Article(null, "Introduction to Spring Boot", "John Doe", "<p> Spring Boot is [...] </p>");

      tuyucheng.createArticle(article);

      assertThat(listener.getEvents())
            .isEmpty();

      assertThat(repository.findAll())
            .hasSize(1)
            .first()
            .extracting(Article::title, Article::author)
            .containsExactly("Introduction to Spring Boot", "John Doe");
   }
}