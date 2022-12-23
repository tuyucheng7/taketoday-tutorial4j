package cn.tuyucheng.taketoday.graphqlreturnmap.resolver;

import cn.tuyucheng.taketoday.graphql.data.Book;
import cn.tuyucheng.taketoday.graphqlreturnmap.entity.Product;
import cn.tuyucheng.taketoday.graphqlreturnmap.repository.ProductRepository;
import cn.tuyucheng.taketoday.graphqlreturnmap.repository.impl.ProductRepositoryImpl;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class Query implements GraphQLQueryResolver {

	@Autowired
	private ProductRepository productRepository;

	public Query() {
		productRepository = new ProductRepositoryImpl();
	}

	public List<Product> getProducts(int pageSize, int pageNumber) {
		return productRepository.getProducts(pageSize, pageNumber);
	}

	public Product getProduct(int id) {
		return productRepository.getProduct(id);
	}

	public List<Book> allBooks() {
		return null;
	}
}