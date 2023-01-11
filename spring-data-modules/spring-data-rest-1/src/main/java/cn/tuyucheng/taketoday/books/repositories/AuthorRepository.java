package cn.tuyucheng.taketoday.books.repositories;

import cn.tuyucheng.taketoday.books.models.Author;
import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<Author, Long> {

}