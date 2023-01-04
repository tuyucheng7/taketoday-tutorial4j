package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.web.dto.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    private List<Book> books = new ArrayList<>();

    public Optional<Book> findById(long id) {
        return books.stream()
              .filter(book -> book.getId() == id)
              .findFirst();
    }

    public void add(Book book) {
        books.add(book);
    }
}