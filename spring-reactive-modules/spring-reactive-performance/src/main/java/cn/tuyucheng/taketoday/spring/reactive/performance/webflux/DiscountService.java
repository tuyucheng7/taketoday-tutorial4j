package cn.tuyucheng.taketoday.spring.reactive.performance.webflux;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

class DiscountService {
   public Mono<BigDecimal> discountForProduct(String productId) {
      return Mono.just(BigDecimal.valueOf(10));
   }
}