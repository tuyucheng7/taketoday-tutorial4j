module cn.tuyucheng.taketoday.dddcontexts.mainapp {
	uses cn.tuyucheng.taketoday.dddcontexts.sharedkernel.events.EventBus;
	uses cn.tuyucheng.taketoday.dddcontexts.ordercontext.service.OrderService;
	uses cn.tuyucheng.taketoday.dddcontexts.ordercontext.repository.CustomerOrderRepository;
	uses cn.tuyucheng.taketoday.dddcontexts.shippingcontext.repository.ShippingOrderRepository;
	uses cn.tuyucheng.taketoday.dddcontexts.shippingcontext.service.ShippingService;
	requires transitive cn.tuyucheng.taketoday.dddcontexts.infrastructure;
}