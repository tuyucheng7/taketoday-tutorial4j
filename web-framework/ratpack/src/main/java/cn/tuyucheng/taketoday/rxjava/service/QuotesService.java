package cn.tuyucheng.taketoday.rxjava.service;

import cn.tuyucheng.taketoday.model.Quote;
import org.reactivestreams.Publisher;
import ratpack.stream.Streams;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;

public class QuotesService {

   private static Random rnd = new Random();
   private static String[] symbols = new String[]{
         "MSFT",
         "ORCL",
         "GOOG",
         "AAPL",
         "CSCO"
   };
   private final ScheduledExecutorService executorService;

   public QuotesService(ScheduledExecutorService executorService) {
      this.executorService = executorService;
   }

   private static Quote randomQuote() {
      return new Quote(
            Instant.now(),
            symbols[rnd.nextInt(symbols.length)],
            Math.round(rnd.nextDouble() * 100)
      );
   }

   public Publisher<Quote> newTicker() {
      return Streams.periodically(executorService, Duration.ofSeconds(2), (t) -> {

         return randomQuote();
      });
   }
}
