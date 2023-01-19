package cn.tuyucheng.taketoday.graphqlvsrest.repository;

import cn.tuyucheng.taketoday.graphqlvsrest.entity.Order;

import java.util.List;

public interface OrderRepository {
	List<Order> getOrdersByProduct(Integer productId);
}