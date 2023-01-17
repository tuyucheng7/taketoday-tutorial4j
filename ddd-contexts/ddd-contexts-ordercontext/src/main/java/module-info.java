import cn.tuyucheng.taketoday.dddcontexts.ordercontext.service.CustomerOrderService;
import cn.tuyucheng.taketoday.dddcontexts.ordercontext.service.OrderService;

module cn.tuyucheng.taketoday.dddcontexts.ordercontext {
	requires cn.tuyucheng.taketoday.dddcontexts.sharedkernel;
	exports cn.tuyucheng.taketoday.dddcontexts.ordercontext.service;
	exports cn.tuyucheng.taketoday.dddcontexts.ordercontext.model;
	exports cn.tuyucheng.taketoday.dddcontexts.ordercontext.repository;
	provides OrderService with CustomerOrderService;
}