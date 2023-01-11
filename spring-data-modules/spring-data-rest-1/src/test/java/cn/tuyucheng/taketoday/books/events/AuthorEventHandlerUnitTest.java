package cn.tuyucheng.taketoday.books.events;

import cn.tuyucheng.taketoday.books.models.Author;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

class AuthorEventHandlerUnitTest {

	@Test
	void whenCreateAuthorThenSuccess() {
		Author author = mock(Author.class);
		AuthorEventHandler authorEventHandler = new AuthorEventHandler();
		authorEventHandler.handleAuthorBeforeCreate(author);
		Mockito.verify(author, Mockito.times(1)).getName();

	}

	@Test
	void whenDeleteAuthorThenSuccess() {
		Author author = mock(Author.class);
		AuthorEventHandler authorEventHandler = new AuthorEventHandler();
		authorEventHandler.handleAuthorAfterDelete(author);
		Mockito.verify(author, Mockito.times(1)).getName();
	}
}