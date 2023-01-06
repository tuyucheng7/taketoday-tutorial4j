package cn.tuyucheng.taketoday.springboothibernate.application.tests;

import cn.tuyucheng.taketoday.springboothibernate.application.models.Book;
import cn.tuyucheng.taketoday.springboothibernate.application.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BookServiceUnitTest {

	@Autowired
	private BookService bookService;

	@Test
	@Sql(scripts = "classpath:import_books.sql")
	void whenApplicationStarts_thenHibernateCreatesInitialRecords() {
		List<Book> books = bookService.list();

		assertEquals(books.size(), 3);
	}
}