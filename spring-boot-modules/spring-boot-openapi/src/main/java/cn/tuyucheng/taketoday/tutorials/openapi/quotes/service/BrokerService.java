package cn.tuyucheng.taketoday.tutorials.openapi.quotes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class BrokerService {

   private final Logger log = LoggerFactory.getLogger(BrokerService.class);

   private final Random rnd = new Random();

   public BrokerService() {
   }

   public BigDecimal getSecurityPrice(@NonNull String symbol) {
      log.info("getSecurityPrice: {}", symbol);
      // Just a mock value
      return BigDecimal.valueOf(100.0 + rnd.nextDouble() * 100.0);
   }
}