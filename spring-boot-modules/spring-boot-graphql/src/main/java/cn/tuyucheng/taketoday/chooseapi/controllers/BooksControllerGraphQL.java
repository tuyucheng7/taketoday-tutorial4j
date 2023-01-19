package cn.tuyucheng.taketoday.chooseapi.controllers;

import cn.tuyucheng.taketoday.chooseapi.dtos.Book;
import cn.tuyucheng.taketoday.chooseapi.services.BooksService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BooksControllerGraphQL {

	private final BooksService booksService;

	public BooksControllerGraphQL(BooksService booksService) {
		this.booksService = booksService;
	}

	@QueryMapping
	public List<Book> books() {
		return booksService.getBooks();
	}
}