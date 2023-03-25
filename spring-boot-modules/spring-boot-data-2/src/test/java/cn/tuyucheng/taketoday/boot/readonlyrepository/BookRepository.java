package cn.tuyucheng.taketoday.boot.readonlyrepository;

import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
}
