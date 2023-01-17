package cn.tuyucheng.taketoday.dddcontexts.ordercontext.repository;

import cn.tuyucheng.taketoday.dddcontexts.ordercontext.model.CustomerOrder;

public interface CustomerOrderRepository {
	void saveCustomerOrder(CustomerOrder order);
}