package cn.tuyucheng.taketoday.messaging.postgresql.service;

import java.math.BigDecimal;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tuyucheng.taketoday.messaging.postgresql.domain.Order;
import cn.tuyucheng.taketoday.messaging.postgresql.domain.OrderType;
import cn.tuyucheng.taketoday.messaging.postgresql.repository.OrdersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {
   private final OrdersRepository repo;
   private final NotifierService notifier;
   private final Cache ordersCache;

   @Transactional
   public Order createOrder(OrderType orderType, String symbol, BigDecimal quantity, BigDecimal price) {
      Order order = new Order();
      order.setOrderType(orderType);
      order.setSymbol(symbol);
      order.setQuantity(quantity);
      order.setPrice(price);
      order = repo.save(order);

      notifier.notifyOrderCreated(order);

      return order;
   }

   @Transactional(readOnly = true)
   public Optional<Order> findById(Long id) {
      Optional<Order> o = Optional.ofNullable(ordersCache.get(id, Order.class));
      if (!o.isEmpty()) {
         LOGGER.info("findById: cache hit, id={}", id);
         return o;
      }

      LOGGER.info("findById: cache miss, id={}", id);
      o = repo.findById(id);
      if (o.isEmpty()) {
         return o;
      }

      ordersCache.put(id, o.get());
      return o;
   }
}