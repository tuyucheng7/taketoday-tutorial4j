package cn.tuyucheng.taketoday.spring.reactive.performance.virtualthreads;

import java.math.BigDecimal;

class DiscountService {

   public BigDecimal discountForProduct(String productId) {
      return BigDecimal.valueOf(10);
   }
}