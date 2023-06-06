package cn.tuyucheng.taketoday.messaging.postgresql.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tuyucheng.taketoday.messaging.postgresql.domain.Order;
import cn.tuyucheng.taketoday.messaging.postgresql.domain.OrderType;
import cn.tuyucheng.taketoday.messaging.postgresql.service.OrdersService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

   private final OrdersService orders;

   @PostMapping("/orders/sell")
   public ResponseEntity<Order> postSellOrder(String symbol, BigDecimal quantity, BigDecimal price) {
      LOGGER.info("postSellOrder: symbol={},quantity={},price={}", symbol, quantity, price);
      Order order = orders.createOrder(OrderType.SELL, symbol, quantity, price);
      return ResponseEntity.status(HttpStatus.CREATED).body(order);
   }

   @PostMapping("/orders/buy")
   public ResponseEntity<Order> postBuyOrder(String symbol, BigDecimal quantity, BigDecimal price) {
      LOGGER.info("postBuyOrder: symbol={},quantity={},price={}", symbol, quantity, price);
      Order order = orders.createOrder(OrderType.BUY, symbol, quantity, price);
      return ResponseEntity.status(HttpStatus.CREATED).body(order);
   }

   @GetMapping("/orders/{id}")
   public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
      Optional<Order> o = orders.findById(id);
      if (o.isEmpty()) {
         return ResponseEntity.notFound().build();
      }

      return ResponseEntity.ok(o.get());
   }
}