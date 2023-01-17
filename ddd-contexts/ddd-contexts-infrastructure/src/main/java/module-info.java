import cn.tuyucheng.taketoday.dddcontexts.infrastructure.db.InMemoryOrderStore;
import cn.tuyucheng.taketoday.dddcontexts.infrastructure.events.SimpleEventBus;

module cn.tuyucheng.taketoday.dddcontexts.infrastructure {
	requires transitive cn.tuyucheng.taketoday.dddcontexts.sharedkernel;
	requires transitive cn.tuyucheng.taketoday.dddcontexts.ordercontext;
	requires transitive cn.tuyucheng.taketoday.dddcontexts.shippingcontext;
	provides cn.tuyucheng.taketoday.dddcontexts.sharedkernel.events.EventBus with SimpleEventBus;
	provides cn.tuyucheng.taketoday.dddcontexts.ordercontext.repository.CustomerOrderRepository with InMemoryOrderStore;
	provides cn.tuyucheng.taketoday.dddcontexts.shippingcontext.repository.ShippingOrderRepository with InMemoryOrderStore;
}