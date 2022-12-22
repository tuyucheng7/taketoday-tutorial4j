package cn.tuyucheng.taketoday.boot.jsp.service;

import cn.tuyucheng.taketoday.boot.jsp.dto.Book;

import java.util.Collection;

public interface BookService {
	Collection<Book> getBooks();

	Book addBook(Book book);
}