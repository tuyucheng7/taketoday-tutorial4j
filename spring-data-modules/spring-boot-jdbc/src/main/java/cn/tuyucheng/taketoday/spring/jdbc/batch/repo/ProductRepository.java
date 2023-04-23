package cn.tuyucheng.taketoday.spring.jdbc.batch.repo;

import cn.tuyucheng.taketoday.spring.jdbc.batch.model.Product;

import java.util.List;

public interface ProductRepository {
	void saveAll(List<Product> products);
}