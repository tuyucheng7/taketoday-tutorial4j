import cn.tuyucheng.taketoday.dddcontexts.shippingcontext.service.ParcelShippingService;
import cn.tuyucheng.taketoday.dddcontexts.shippingcontext.service.ShippingService;

module cn.tuyucheng.taketoday.dddcontexts.shippingcontext {
	requires cn.tuyucheng.taketoday.dddcontexts.sharedkernel;
	exports cn.tuyucheng.taketoday.dddcontexts.shippingcontext.service;
	exports cn.tuyucheng.taketoday.dddcontexts.shippingcontext.model;
	exports cn.tuyucheng.taketoday.dddcontexts.shippingcontext.repository;
	provides ShippingService with ParcelShippingService;
}