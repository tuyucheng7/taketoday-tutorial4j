package cn.tuyucheng.taketoday.orderservice.client;

public interface OrderClient {

	OrderResponse order(OrderDTO orderDTO);
}