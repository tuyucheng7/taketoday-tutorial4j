package cn.tuyucheng.taketoday.bookstore;

import java.util.Random;

public class LibraryAdmin {

	public Book createBook(String title, String author) {

		final long isbn = new Random().nextLong();

		return new Book(title, author, isbn);
	}

}