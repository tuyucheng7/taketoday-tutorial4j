package cn.tuyucheng.taketoday.tutorials.openapi.quotes.controller;

import cn.tuyucheng.taketoday.tutorials.openapi.quotes.api.QuotesApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuotesApiImplUnitTest {

   @Autowired
   private QuotesApi api;

   private static Instant NOW = Instant.now();

   @Test
   void whenGetQuote_then_success() {
      var response = api.getQuote("GOOG");
      assertThat(response)
            .isNotNull();

      assertThat(response.getStatusCode().is2xxSuccessful())
            .isTrue();

      assertThat(response.getBody().getTimestamp())
            .isEqualTo(OffsetDateTime.ofInstant(NOW, ZoneId.systemDefault()));
   }

   @TestConfiguration
   @EnableCaching
   static class TestConfig {

      @Bean
      @Primary
      Clock fixedClock() {
         return Clock.fixed(NOW, ZoneId.systemDefault());
      }
   }
}