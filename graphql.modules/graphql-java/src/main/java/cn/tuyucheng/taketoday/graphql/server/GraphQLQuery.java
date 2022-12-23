package cn.tuyucheng.taketoday.graphql.server;

import cn.tuyucheng.taketoday.graphql.data.Book;
import cn.tuyucheng.taketoday.graphql.data.BookRepository;
import cn.tuyucheng.taketoday.graphqlreturnmap.entity.Product;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import java.util.List;

public class GraphQLQuery implements GraphQLQueryResolver {

	private final BookRepository repository;

	public GraphQLQuery(BookRepository repository) {
		this.repository = repository;
	}

	public List<Book> allBooks() {
		return repository.getAllBooks();
	}

	public List<Product> getProducts(int pageSize, int pageNumber) {
		return null;
	}

	public Product getProduct(int id) {
		return null;
	}
}