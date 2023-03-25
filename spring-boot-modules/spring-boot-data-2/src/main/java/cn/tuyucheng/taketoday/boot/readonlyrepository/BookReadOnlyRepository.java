package cn.tuyucheng.taketoday.boot.readonlyrepository;

import java.util.List;

public interface BookReadOnlyRepository extends ReadOnlyRepository<Book, Long> {

	List<Book> findByAuthor(String author);

	List<Book> findByTitle(String title);
}
