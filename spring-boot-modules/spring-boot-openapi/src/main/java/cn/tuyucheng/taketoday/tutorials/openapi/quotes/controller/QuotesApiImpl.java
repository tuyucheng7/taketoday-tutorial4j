package cn.tuyucheng.taketoday.tutorials.openapi.quotes.controller;

import cn.tuyucheng.taketoday.tutorials.openapi.quotes.service.BrokerService;
import cn.tuyucheng.taketoday.tutorials.openapi.quotes.api.QuotesApi;
import cn.tuyucheng.taketoday.tutorials.openapi.quotes.api.QuotesApiDelegate;
import cn.tuyucheng.taketoday.tutorials.openapi.quotes.api.model.QuoteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;

@Component
public class QuotesApiImpl implements QuotesApiDelegate {
   private final BrokerService broker;
   private final Clock clock;

   public QuotesApiImpl(BrokerService broker, Clock clock) {
      this.broker = broker;
      this.clock = clock;
   }

   /**
    * GET /quotes/{symbol} : Get current quote for a security
    *
    * @param symbol Security&#39;s symbol (required)
    * @return OK (status code 200)
    * @see QuotesApi#getQuote
    */
   @Override
   public ResponseEntity<QuoteResponse> getQuote(String symbol) {
      var price = broker.getSecurityPrice(symbol);

      var quote = new QuoteResponse();
      quote.setSymbol(symbol);
      quote.setPrice(price);
      quote.setTimestamp(OffsetDateTime.now(clock));
      return ResponseEntity.ok(quote);
   }
}