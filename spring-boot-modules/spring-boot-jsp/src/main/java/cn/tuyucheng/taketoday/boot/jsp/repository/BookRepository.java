package cn.tuyucheng.taketoday.boot.jsp.repository;

import cn.tuyucheng.taketoday.boot.jsp.repository.model.BookData;

import java.util.Collection;
import java.util.Optional;

public interface BookRepository {
	Collection<BookData> findAll();

	Optional<BookData> findById(String isbn);

	BookData add(BookData book);
}