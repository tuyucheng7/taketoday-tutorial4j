package cn.tuyucheng.taketoday.graphqlvsrest.controller;

import cn.tuyucheng.taketoday.graphqlvsrest.entity.Order;
import cn.tuyucheng.taketoday.graphqlvsrest.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {

   @Autowired
   OrderRepository orderRepository;

   @GetMapping()
   public List<Order> getOrders(@RequestParam("product-id") Integer productId) {
      return orderRepository.getOrdersByProduct(productId);
   }
}