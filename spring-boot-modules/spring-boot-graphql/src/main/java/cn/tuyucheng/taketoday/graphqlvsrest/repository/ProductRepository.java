package cn.tuyucheng.taketoday.graphqlvsrest.repository;

import cn.tuyucheng.taketoday.graphqlvsrest.entity.Product;
import cn.tuyucheng.taketoday.graphqlvsrest.model.ProductModel;

import java.util.List;

public interface ProductRepository {
	List<Product> getProducts(Integer pageSize, Integer pageNumber);

	Product getProduct(Integer id);

	Product save(ProductModel productModel);

	Product update(Integer productId, ProductModel productModel);
}