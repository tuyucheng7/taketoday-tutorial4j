package cn.tuyucheng.taketoday.reactive.service;

import cn.tuyucheng.taketoday.constants.OrderStatus;
import cn.tuyucheng.taketoday.domain.Order;
import cn.tuyucheng.taketoday.domain.Product;
import cn.tuyucheng.taketoday.reactive.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

	@Autowired
	ProductRepository productRepository;

	@Transactional
	public Mono<Order> handleOrder(Order order) {
		LOGGER.info("Handle order invoked with: {}", order);
		return Flux.fromIterable(order.getLineItems())
			.flatMap(l -> productRepository.findById(l.getProductId()))
			.flatMap(p -> {
				int q = order.getLineItems()
					.stream()
					.filter(l -> l.getProductId()
						.equals(p.getId()))
					.findAny()
					.get()
					.getQuantity();
				if (p.getStock() >= q) {
					p.setStock(p.getStock() - q);
					return productRepository.save(p);
				} else {
					return Mono.error(new RuntimeException("Product is out of stock: " + p.getId()));
				}
			})
			.then(Mono.just(order.setOrderStatus(OrderStatus.SUCCESS)));
	}

	@Transactional
	public Mono<Order> revertOrder(Order order) {
		LOGGER.info("Revert order invoked with: {}", order);
		return Flux.fromIterable(order.getLineItems())
			.flatMap(l -> productRepository.findById(l.getProductId()))
			.flatMap(p -> {
				int q = order.getLineItems()
					.stream()
					.filter(l -> l.getProductId()
						.equals(p.getId()))
					.collect(Collectors.toList())
					.get(0)
					.getQuantity();

				p.setStock(p.getStock() + q);
				return productRepository.save(p);
			})
			.then(Mono.just(order.setOrderStatus(OrderStatus.SUCCESS)));
	}

	public Flux<Product> getProducts() {
		return productRepository.findAll();
	}

}
