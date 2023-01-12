package cn.tuyucheng.taketoday.services;

import cn.tuyucheng.taketoday.persistence.model.Order;

import java.util.List;

public interface OrderService {

	List<Order> getAllOrdersForCustomer(String customerId);

	Order getOrderByIdForCustomer(String customerId, String orderId);

}
