package cn.tuyucheng.taketoday.springboothibernate.application.services;

import cn.tuyucheng.taketoday.springboothibernate.application.models.Book;
import cn.tuyucheng.taketoday.springboothibernate.application.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;

	public List<Book> list() {
		return bookRepository.findAll();
	}
}