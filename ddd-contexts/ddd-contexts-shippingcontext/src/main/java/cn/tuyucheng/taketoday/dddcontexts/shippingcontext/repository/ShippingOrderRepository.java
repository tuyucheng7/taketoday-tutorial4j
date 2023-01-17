package cn.tuyucheng.taketoday.dddcontexts.shippingcontext.repository;

import cn.tuyucheng.taketoday.dddcontexts.shippingcontext.model.ShippableOrder;

import java.util.Optional;

public interface ShippingOrderRepository {
	Optional<ShippableOrder> findShippableOrder(int orderId);
}