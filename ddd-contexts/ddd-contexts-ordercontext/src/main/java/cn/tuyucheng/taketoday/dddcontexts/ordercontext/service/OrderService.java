package cn.tuyucheng.taketoday.dddcontexts.ordercontext.service;

import cn.tuyucheng.taketoday.dddcontexts.ordercontext.model.CustomerOrder;
import cn.tuyucheng.taketoday.dddcontexts.ordercontext.repository.CustomerOrderRepository;
import cn.tuyucheng.taketoday.dddcontexts.sharedkernel.service.ApplicationService;

public interface OrderService extends ApplicationService {
	void placeOrder(CustomerOrder order);

	void setOrderRepository(CustomerOrderRepository orderRepository);
}