package cn.tuyucheng.taketoday.redistestcontainers.service;

import cn.tuyucheng.taketoday.redistestcontainers.repository.ProductRepository;
import cn.tuyucheng.taketoday.redistestcontainers.hash.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product getProduct(String id) {
		return productRepository.findById(id).orElse(null);
	}

	void createProduct(Product product) {
		productRepository.save(product);
	}

	void updateProduct(Product product) {
		productRepository.save(product);
	}

	void deleteProduct(String id) {
		productRepository.deleteById(id);
	}
}