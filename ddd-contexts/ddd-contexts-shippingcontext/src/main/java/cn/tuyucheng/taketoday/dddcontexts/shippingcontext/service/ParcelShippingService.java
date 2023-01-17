package cn.tuyucheng.taketoday.dddcontexts.shippingcontext.service;

import cn.tuyucheng.taketoday.dddcontexts.sharedkernel.events.ApplicationEvent;
import cn.tuyucheng.taketoday.dddcontexts.sharedkernel.events.EventBus;
import cn.tuyucheng.taketoday.dddcontexts.sharedkernel.events.EventSubscriber;
import cn.tuyucheng.taketoday.dddcontexts.shippingcontext.model.Parcel;
import cn.tuyucheng.taketoday.dddcontexts.shippingcontext.model.ShippableOrder;
import cn.tuyucheng.taketoday.dddcontexts.shippingcontext.repository.ShippingOrderRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParcelShippingService implements ShippingService {
	public static final String EVENT_ORDER_READY_FOR_SHIPMENT = "OrderReadyForShipmentEvent";
	private ShippingOrderRepository orderRepository;
	private EventBus eventBus;
	private Map<Integer, Parcel> shippedParcels = new HashMap<>();

	@Override
	public void shipOrder(int orderId) {
		Optional<ShippableOrder> order = this.orderRepository.findShippableOrder(orderId);
		order.ifPresent(completedOrder -> {
			Parcel parcel = new Parcel(completedOrder.getOrderId(), completedOrder.getAddress(), completedOrder.getPackageItems());
			if (parcel.isTaxable()) {
				// Calculate additional taxes
			}
			// Ship parcel
			this.shippedParcels.put(completedOrder.getOrderId(), parcel);
		});
	}

	@Override
	public void listenToOrderEvents() {
		this.eventBus.subscribe(EVENT_ORDER_READY_FOR_SHIPMENT, new EventSubscriber() {
			@Override
			public <E extends ApplicationEvent> void onEvent(E event) {
				shipOrder(Integer.parseInt(event.getPayloadValue("order_id")));
			}
		});
	}

	@Override
	public Optional<Parcel> getParcelByOrderId(int orderId) {
		return Optional.ofNullable(this.shippedParcels.get(orderId));
	}

	public void setOrderRepository(ShippingOrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
}