package cn.tuyucheng.taketoday.dddcontexts.mainapp;

import cn.tuyucheng.taketoday.dddcontexts.ordercontext.model.CustomerOrder;
import cn.tuyucheng.taketoday.dddcontexts.ordercontext.model.OrderItem;
import cn.tuyucheng.taketoday.dddcontexts.ordercontext.repository.CustomerOrderRepository;
import cn.tuyucheng.taketoday.dddcontexts.ordercontext.service.OrderService;
import cn.tuyucheng.taketoday.dddcontexts.sharedkernel.events.EventBus;
import cn.tuyucheng.taketoday.dddcontexts.shippingcontext.repository.ShippingOrderRepository;
import cn.tuyucheng.taketoday.dddcontexts.shippingcontext.service.ShippingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class Application {

	public static void main(String[] args) {
		Map<Class<?>, Object> container = createContainer();
		OrderService orderService = (OrderService) container.get(OrderService.class);
		ShippingService shippingService = (ShippingService) container.get(ShippingService.class);
		shippingService.listenToOrderEvents();

		CustomerOrder customerOrder = new CustomerOrder();
		int orderId = 1;
		customerOrder.setOrderId(orderId);
		List<OrderItem> orderItems = new ArrayList<>();
		orderItems.add(new OrderItem(1, 2, 3, 1));
		orderItems.add(new OrderItem(2, 1, 1, 1));
		orderItems.add(new OrderItem(3, 4, 11, 21));
		customerOrder.setOrderItems(orderItems);
		customerOrder.setPaymentMethod("PayPal");
		customerOrder.setAddress("Full address here");
		orderService.placeOrder(customerOrder);

		if (orderId == shippingService.getParcelByOrderId(orderId).get().getOrderId()) {
			System.out.println("Order has been processed and shipped successfully");
		}
	}

	public static Map<Class<?>, Object> createContainer() {
		EventBus eventBus = ServiceLoader.load(EventBus.class).findFirst().get();
		CustomerOrderRepository customerOrderRepository = ServiceLoader.load(CustomerOrderRepository.class).findFirst().get();
		ShippingOrderRepository shippingOrderRepository = ServiceLoader.load(ShippingOrderRepository.class).findFirst().get();
		ShippingService shippingService = ServiceLoader.load(ShippingService.class).findFirst().get();
		shippingService.setEventBus(eventBus);
		shippingService.setOrderRepository(shippingOrderRepository);
		OrderService orderService = ServiceLoader.load(OrderService.class).findFirst().get();
		orderService.setEventBus(eventBus);
		orderService.setOrderRepository(customerOrderRepository);
		HashMap<Class<?>, Object> container = new HashMap<>();
		container.put(OrderService.class, orderService);
		container.put(ShippingService.class, shippingService);
		return container;
	}
}