package cn.tuyucheng.taketoday.spqr;

import java.util.List;

public interface BookService {
    Book getBookWithTitle(String title);

    List<Book> getAllBooks();

    Book addBook(Book book);

    Book updateBook(Book book);

    boolean deleteBook(Book book);
}