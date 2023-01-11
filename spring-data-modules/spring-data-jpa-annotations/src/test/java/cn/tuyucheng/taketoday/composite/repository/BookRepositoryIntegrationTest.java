package cn.tuyucheng.taketoday.composite.repository;

import cn.tuyucheng.taketoday.composite.BookApplication;
import cn.tuyucheng.taketoday.composite.entity.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BookApplication.class)
class BookRepositoryIntegrationTest {

	static final String JAVA_101 = "Java101";
	static final String JANE = "Jane";
	static final String TECH = "Tech";
	@Autowired
	BookRepository repository;

	@BeforeEach
	void setUp() {
		Book book1 = new Book("John", JAVA_101, TECH, 20);
		Book book2 = new Book(JANE, JAVA_101, "Arch", 25);
		Book book3 = new Book(JANE, "Scala101", TECH, 23);

		repository.saveAll(Arrays.asList(book1, book2, book3));
	}

	@AfterEach
	void tearDown() {
		repository.deleteAll();
	}

	@Test
	void testFindByName() {
		List<Book> books = repository.findByIdName(JAVA_101);

		assertEquals(2, books.size());
	}

	@Test
	void testFindByAuthor() {
		List<Book> books = repository.findByIdAuthor(JANE);

		assertEquals(2, books.size());
	}

	@Test
	void testFindByGenre() {
		List<Book> books = repository.findByGenre(TECH);

		assertEquals(2, books.size());
	}
}