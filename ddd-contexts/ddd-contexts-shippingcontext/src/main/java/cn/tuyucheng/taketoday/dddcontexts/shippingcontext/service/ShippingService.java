package cn.tuyucheng.taketoday.dddcontexts.shippingcontext.service;

import cn.tuyucheng.taketoday.dddcontexts.sharedkernel.service.ApplicationService;
import cn.tuyucheng.taketoday.dddcontexts.shippingcontext.model.Parcel;
import cn.tuyucheng.taketoday.dddcontexts.shippingcontext.repository.ShippingOrderRepository;

import java.util.Optional;

public interface ShippingService extends ApplicationService {
	void shipOrder(int orderId);

	void listenToOrderEvents();

	Optional<Parcel> getParcelByOrderId(int orderId);

	void setOrderRepository(ShippingOrderRepository orderRepository);
}