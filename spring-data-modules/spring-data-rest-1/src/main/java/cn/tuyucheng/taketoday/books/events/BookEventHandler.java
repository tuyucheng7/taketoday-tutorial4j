package cn.tuyucheng.taketoday.books.events;

import cn.tuyucheng.taketoday.books.models.Author;
import cn.tuyucheng.taketoday.books.models.Book;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.util.logging.Logger;

@RepositoryEventHandler
public class BookEventHandler {
	Logger logger = Logger.getLogger("Class BookEventHandler");

	@HandleBeforeCreate
	public void handleBookBeforeCreate(Book book) {
		logger.info("Inside Book Before Create ....");
		book.getAuthors();
	}

	@HandleBeforeCreate
	public void handleAuthorBeforeCreate(Author author) {
		logger.info("Inside Author Before Create ....");
		author.getBooks();
	}
}