package cn.tuyucheng.taketoday.axon.querymodel;

import cn.tuyucheng.taketoday.axon.coreapi.events.OrderConfirmedEvent;
import cn.tuyucheng.taketoday.axon.coreapi.events.OrderCreatedEvent;
import cn.tuyucheng.taketoday.axon.coreapi.events.OrderShippedEvent;
import cn.tuyucheng.taketoday.axon.coreapi.events.ProductAddedEvent;
import cn.tuyucheng.taketoday.axon.coreapi.events.ProductCountDecrementedEvent;
import cn.tuyucheng.taketoday.axon.coreapi.events.ProductCountIncrementedEvent;
import cn.tuyucheng.taketoday.axon.coreapi.events.ProductRemovedEvent;
import cn.tuyucheng.taketoday.axon.coreapi.queries.FindAllOrderedProductsQuery;
import cn.tuyucheng.taketoday.axon.coreapi.queries.Order;
import cn.tuyucheng.taketoday.axon.coreapi.queries.OrderUpdatesQuery;
import cn.tuyucheng.taketoday.axon.coreapi.queries.TotalProductsShippedQuery;
import org.reactivestreams.Publisher;

import java.util.List;

public interface OrdersEventHandler {

	void on(OrderCreatedEvent event);

	void on(ProductAddedEvent event);

	void on(ProductCountIncrementedEvent event);

	void on(ProductCountDecrementedEvent event);

	void on(ProductRemovedEvent event);

	void on(OrderConfirmedEvent event);

	void on(OrderShippedEvent event);

	List<Order> handle(FindAllOrderedProductsQuery query);

	Publisher<Order> handleStreaming(FindAllOrderedProductsQuery query);

	Integer handle(TotalProductsShippedQuery query);

	Order handle(OrderUpdatesQuery query);

	void reset(List<Order> orderList);
}