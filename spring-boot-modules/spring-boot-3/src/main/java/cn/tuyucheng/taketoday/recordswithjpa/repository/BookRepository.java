package cn.tuyucheng.taketoday.recordswithjpa.repository;

import cn.tuyucheng.taketoday.recordswithjpa.entity.Book;
import cn.tuyucheng.taketoday.recordswithjpa.records.BookRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends CrudRepository<Book, Long> {
	List<BookRecord> findBookByAuthor(String author);

	@Query("SELECT new cn.tuyucheng.taketoday.recordswithjpa.records.BookRecord(b.id, b.title, b.author, b.isbn) " +
		"FROM Book b WHERE b.id = :id")
	BookRecord findBookById(@Param("id") Long id);
}