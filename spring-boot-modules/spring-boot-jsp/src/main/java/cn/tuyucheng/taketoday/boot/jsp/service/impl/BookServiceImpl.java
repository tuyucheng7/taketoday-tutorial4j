package cn.tuyucheng.taketoday.boot.jsp.service.impl;

import cn.tuyucheng.taketoday.boot.jsp.dto.Book;
import cn.tuyucheng.taketoday.boot.jsp.exception.DuplicateBookException;
import cn.tuyucheng.taketoday.boot.jsp.repository.BookRepository;
import cn.tuyucheng.taketoday.boot.jsp.repository.model.BookData;
import cn.tuyucheng.taketoday.boot.jsp.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;

	public BookServiceImpl(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Override
	public Collection<Book> getBooks() {
		return bookRepository.findAll()
			.stream()
			.map(BookServiceImpl::convertBookData)
			.collect(Collectors.toList());
	}

	@Override
	public Book addBook(Book book) {
		final Optional<BookData> existingBook = bookRepository.findById(book.getIsbn());
		if (existingBook.isPresent()) {
			throw new DuplicateBookException(book);
		}

		final BookData savedBook = bookRepository.add(convertBook(book));
		return convertBookData(savedBook);
	}

	private static Book convertBookData(BookData bookData) {
		return new Book(bookData.getIsbn(), bookData.getName(), bookData.getAuthor());
	}

	private static BookData convertBook(Book book) {
		return new BookData(book.getIsbn(), book.getName(), book.getAuthor());
	}
}