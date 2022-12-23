package cn.tuyucheng.taketoday.graphqlreturnmap.repository;


import cn.tuyucheng.taketoday.graphqlreturnmap.entity.Product;

import java.util.List;

public interface ProductRepository {
	List<Product> getProducts(Integer pageSize, Integer pageNumber);

	Product getProduct(Integer id);
}