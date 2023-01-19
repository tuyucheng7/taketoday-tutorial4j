package cn.tuyucheng.taketoday.graphqlvsrest.controller;

import cn.tuyucheng.taketoday.graphqlvsrest.entity.Order;
import cn.tuyucheng.taketoday.graphqlvsrest.entity.Product;
import cn.tuyucheng.taketoday.graphqlvsrest.model.ProductModel;
import cn.tuyucheng.taketoday.graphqlvsrest.repository.OrderRepository;
import cn.tuyucheng.taketoday.graphqlvsrest.repository.ProductRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductGraphQLController {

	private final ProductRepository productRepository;

	private final OrderRepository orderRepository;

	public ProductGraphQLController(ProductRepository productRepository, OrderRepository orderRepository) {
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
	}

	@QueryMapping
	public List<Product> products(@Argument int size, @Argument int page) {
		return productRepository.getProducts(size, page);
	}

	@QueryMapping
	public Product product(@Argument int id) {
		return productRepository.getProduct(id);
	}

	@MutationMapping
	public Product saveProduct(@Argument ProductModel product) {
		return productRepository.save(product);
	}

	@MutationMapping
	public Product updateProduct(@Argument Integer id, @Argument ProductModel product) {
		return productRepository.update(id, product);
	}

	@SchemaMapping(typeName = "Product", field = "orders")
	public List<Order> getOrders(Product product) {
		return orderRepository.getOrdersByProduct(product.getId());
	}
}