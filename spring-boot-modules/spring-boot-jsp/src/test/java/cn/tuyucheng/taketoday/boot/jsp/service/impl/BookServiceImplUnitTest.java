package cn.tuyucheng.taketoday.boot.jsp.service.impl;

import cn.tuyucheng.taketoday.boot.jsp.dto.Book;
import cn.tuyucheng.taketoday.boot.jsp.exception.DuplicateBookException;
import cn.tuyucheng.taketoday.boot.jsp.repository.BookRepository;
import cn.tuyucheng.taketoday.boot.jsp.repository.model.BookData;
import cn.tuyucheng.taketoday.boot.jsp.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplUnitTest {

	@Mock
	private BookRepository bookRepository;

	@Test
	void whenGetBooks_thenAllBooksReturned() {
		when(bookRepository.findAll()).thenReturn(existingBooks());
		BookService bookService = new BookServiceImpl(bookRepository);

		Collection<Book> storedBooks = bookService.getBooks();
		assertEquals(3, storedBooks.size());
		assertThat(storedBooks, hasItem(hasProperty("isbn", equalTo("isbn1"))));
		assertThat(storedBooks, hasItem(hasProperty("isbn", equalTo("isbn2"))));
		assertThat(storedBooks, hasItem(hasProperty("isbn", equalTo("isbn3"))));
	}

	@Test
	void whenAddBook_thenAddSuccessful() {
		when(bookRepository.findById(anyString())).thenReturn(Optional.empty());
		when(bookRepository.add(any(BookData.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
		BookService bookService = new BookServiceImpl(bookRepository);
		Book book = bookService.addBook(new Book("isbn1", "name1", "author1"));

		assertEquals("isbn1", book.getIsbn());
		assertEquals("name1", book.getName());
		assertEquals("author1", book.getAuthor());
	}

	@Test
	void givenBookAlreadyExist_whenAddBook_thenDuplicateBookException() {
		BookData existingBook = new BookData("isbn1", "name1", "author1");
		when(bookRepository.findById("isbn1")).thenReturn(Optional.of(existingBook));
		BookService bookService = new BookServiceImpl(bookRepository);
		Book bookToBeAdded = new Book("isbn1", "name1", "author1");

		assertThrows(DuplicateBookException.class, () -> bookService.addBook(bookToBeAdded));
	}

	private static Collection<BookData> existingBooks() {
		List<BookData> books = new ArrayList<>();
		books.add(new BookData("isbn1", "name1", "author1"));
		books.add(new BookData("isbn2", "name2", "author2"));
		books.add(new BookData("isbn3", "name3", "author3"));
		return books;
	}
}