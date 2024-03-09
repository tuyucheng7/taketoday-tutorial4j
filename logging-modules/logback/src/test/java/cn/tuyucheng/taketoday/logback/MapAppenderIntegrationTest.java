package cn.tuyucheng.taketoday.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MapAppenderIntegrationTest {

   private Logger logger;

   @BeforeEach
   void setUp() {
      logger = (Logger) LoggerFactory.getLogger(MapAppenderIntegrationTest.class);
   }

   @Test
   void whenLoggerEmitsLoggingEvent_thenAppenderReceivesEvent() {
      logger.info("Test from {}", this.getClass().getSimpleName());
      MapAppender appender = (MapAppender) logger.getAppender("map");

      List<String> messages = appender.getEventMap().values().stream().map(ILoggingEvent::getMessage).collect(toList());
      assertThat(messages, hasItems("Test from {}"));
   }

   @Test
   void givenNoPrefixSet_whenLoggerEmitsEvent_thenAppenderReceivesNoEvent() {
      logger.info("Test from {}", this.getClass().getSimpleName());
      MapAppender appender = (MapAppender) logger.getAppender("badMap");
      assertEquals(appender.getEventMap().size(), 0);
   }
}