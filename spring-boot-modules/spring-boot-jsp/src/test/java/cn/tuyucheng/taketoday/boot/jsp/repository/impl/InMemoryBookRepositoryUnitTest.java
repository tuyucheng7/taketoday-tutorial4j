package cn.tuyucheng.taketoday.boot.jsp.repository.impl;

import cn.tuyucheng.taketoday.boot.jsp.repository.BookRepository;
import cn.tuyucheng.taketoday.boot.jsp.repository.model.BookData;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryBookRepositoryUnitTest {

	@Test
	void givenEmptyData_whenFindAll_thenReturnEmptyCollection() {
		BookRepository bookRepository = new InMemoryBookRepository(Collections.emptyMap());
		Collection<BookData> storedBooks = bookRepository.findAll();

		assertEquals(0, storedBooks.size());
	}

	@Test
	void givenInitialData_whenFindAll_thenReturnInitialData() {
		BookRepository bookRepository = new InMemoryBookRepository(initialBookData());
		Collection<BookData> storedBooks = bookRepository.findAll();

		assertEquals(3, storedBooks.size());
	}

	@Test
	void givenInitialData_whenFindUnavailableIsbn_thenReturnEmpty() {
		BookRepository bookRepository = new InMemoryBookRepository(initialBookData());
		Optional<BookData> storedBookOpt = bookRepository.findById("isbn4");

		assertFalse(storedBookOpt.isPresent());
	}

	@Test
	void givenInitialData_whenFindAvailableIsbn_thenReturnItem() {
		BookRepository bookRepository = new InMemoryBookRepository(initialBookData());
		Optional<BookData> storedBookOpt = bookRepository.findById("isbn1");

		assertTrue(storedBookOpt.isPresent());
	}

	@Test
	void givenAddedIsbn_whenFindAvailableIsbn_thenReturnItem() {
		BookRepository bookRepository = new InMemoryBookRepository(Collections.emptyMap());
		bookRepository.add(new BookData("isbn4", "name4", "author4"));
		Optional<BookData> storedBookOpt = bookRepository.findById("isbn4");

		assertTrue(storedBookOpt.isPresent());
	}

	private static Map<String, BookData> initialBookData() {
		Map<String, BookData> initData = new HashMap<>();
		initData.put("isbn1", new BookData("isbn1", "name1", "author1"));
		initData.put("isbn2", new BookData("isbn2", "name2", "author2"));
		initData.put("isbn3", new BookData("isbn3", "name3", "author3"));
		return initData;
	}
}