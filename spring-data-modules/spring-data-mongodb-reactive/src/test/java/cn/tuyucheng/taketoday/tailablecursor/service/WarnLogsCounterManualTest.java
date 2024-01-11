package cn.tuyucheng.taketoday.tailablecursor.service;

import cn.tuyucheng.taketoday.tailablecursor.LogsCounterApplication;
import cn.tuyucheng.taketoday.tailablecursor.domain.Log;
import cn.tuyucheng.taketoday.tailablecursor.domain.LogLevel;
import cn.tuyucheng.taketoday.tailablecursor.repository.LogsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LogsCounterApplication.class)
@Slf4j
class WarnLogsCounterManualTest {
   @Autowired
   private LogsRepository repository;

   @Autowired
   private ReactiveMongoTemplate template;

   @BeforeEach
   void setUp() {
      createCappedCollectionUsingReactiveMongoTemplate(template);

      persistDocument(Log.builder()
            .level(LogLevel.WARN)
            .service("Service 1")
            .message("Initial Warn message")
            .build());
   }

   private void createCappedCollectionUsingReactiveMongoTemplate(ReactiveMongoTemplate reactiveMongoTemplate) {
      reactiveMongoTemplate.dropCollection(Log.class).block();
      reactiveMongoTemplate.createCollection(Log.class, CollectionOptions.empty()
            .maxDocuments(5)
            .size(1024 * 1024L)
            .capped()).block();
   }

   private void persistDocument(Log log) {
      repository.save(log).block();
   }

   @Test
   void whenWarnLogsArePersisted_thenTheyAreReceivedByLogsCounter() throws Exception {
      WarnLogsCounter warnLogsCounter = new WarnLogsCounter(template);

      Thread.sleep(1000L); // wait for initialization

      Flux.range(0, 10)
            .map(i -> Log.builder()
                  .level(i > 5 ? LogLevel.WARN : LogLevel.INFO)
                  .service("some-service")
                  .message("some log message")
                  .build())
            .map(entity -> repository.save(entity).subscribe())
            .blockLast();

      Thread.sleep(1000L); // wait to receive all messages from the reactive mongodb driver

      assertThat(warnLogsCounter.count(), is(5));
      warnLogsCounter.close();
   }
}