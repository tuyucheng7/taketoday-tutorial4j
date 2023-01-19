package cn.tuyucheng.taketoday.quarkus.repository;

import cn.tuyucheng.taketoday.quarkus.model.Book;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

import static io.quarkus.panache.common.Parameters.with;

@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {

	public Stream<Book> findBy(String query) {
		return find("author like :query or title like :query",
			with("query", "%" + query + "%")).stream();
	}
}