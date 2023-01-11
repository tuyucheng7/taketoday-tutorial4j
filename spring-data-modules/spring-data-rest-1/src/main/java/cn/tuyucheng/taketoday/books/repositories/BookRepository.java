package cn.tuyucheng.taketoday.books.repositories;

import cn.tuyucheng.taketoday.books.models.Book;
import cn.tuyucheng.taketoday.books.projections.CustomBook;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = CustomBook.class)
public interface BookRepository extends CrudRepository<Book, Long> {
}