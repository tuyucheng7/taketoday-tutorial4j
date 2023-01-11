package cn.tuyucheng.taketoday.books.projections;

import cn.tuyucheng.taketoday.books.SpringDataRestApplication;
import cn.tuyucheng.taketoday.books.models.Author;
import cn.tuyucheng.taketoday.books.models.Book;
import cn.tuyucheng.taketoday.books.repositories.AuthorRepository;
import cn.tuyucheng.taketoday.books.repositories.BookRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringDataRestApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
class SpringDataProjectionLiveTest {
	private static final String BOOK_ENDPOINT = "http://localhost:8080/books";
	private static final String AUTHOR_ENDPOINT = "http://localhost:8080/authors";

	@Autowired
	private BookRepository bookRepo;

	@Autowired
	private AuthorRepository authorRepo;

	@BeforeEach
	void setup() {
		if (!bookRepo.findById(1L).isPresent()) {
			Book book = new Book("Animal Farm");
			book.setIsbn("978-1943138425");
			book = bookRepo.save(book);
			Author author = new Author("George Orwell");
			author = authorRepo.save(author);
			author.setBooks(List.of(book));
			author = authorRepo.save(author);
		}
	}

	@Test
	void whenGetBook_thenOK() {
		final Response response = RestAssured.get(BOOK_ENDPOINT + "/1");

		assertEquals(200, response.getStatusCode());
		assertTrue(response.asString().contains("isbn"));
		assertFalse(response.asString().contains("authorCount"));
		// System.out.println(response.asString());
	}

	@Test
	void whenGetBookProjection_thenOK() {
		final Response response = RestAssured.get(BOOK_ENDPOINT + "/1?projection=customBook");

		assertEquals(200, response.getStatusCode());
		assertFalse(response.asString().contains("isbn"));
		assertTrue(response.asString().contains("authorCount"));
		// System.out.println(response.asString());
	}

	@Test
	void whenGetAllBooks_thenOK() {
		final Response response = RestAssured.get(BOOK_ENDPOINT);

		assertEquals(200, response.getStatusCode());
		assertFalse(response.asString().contains("isbn"));
		assertTrue(response.asString().contains("authorCount"));
		// System.out.println(response.asString());
	}

	@Test
	void whenGetAuthorBooks_thenOK() {
		final Response response = RestAssured.get(AUTHOR_ENDPOINT + "/1/books");

		assertEquals(200, response.getStatusCode());
		assertFalse(response.asString().contains("isbn"));
		assertTrue(response.asString().contains("authorCount"));
		System.out.println(response.asString());
	}
}