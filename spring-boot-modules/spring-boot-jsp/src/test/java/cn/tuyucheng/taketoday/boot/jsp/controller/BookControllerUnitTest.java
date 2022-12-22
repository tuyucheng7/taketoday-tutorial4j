package cn.tuyucheng.taketoday.boot.jsp.controller;

import cn.tuyucheng.taketoday.boot.jsp.dto.Book;
import cn.tuyucheng.taketoday.boot.jsp.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookController.class)
class BookControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookService;

	@Test
	void whenViewBooks_thenReturnBooksView() throws Exception {
		when(bookService.getBooks()).thenReturn(existingBooks());
		ResultActions viewBooksResult = mockMvc.perform(get("/book/viewBooks"));

		viewBooksResult
			.andExpect(view().name("view-books"))
			.andExpect(model().attribute("books", hasSize(3)));
	}

	@Test
	void whenAddBookView_thenReturnAddBooksView() throws Exception {
		ResultActions addBookViewResult = mockMvc.perform(get("/book/addBook"));

		addBookViewResult
			.andExpect(view().name("add-book"))
			.andExpect(model().attribute("book", isA(Book.class)));
	}

	@Test
	void whenAddBookPost_thenRedirectToAddBookView() throws Exception {
		when(bookService.addBook(any(Book.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
		MockHttpServletRequestBuilder addBookRequest = MockMvcRequestBuilders.post("/book/addBook")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.param("isbn", "isbn1")
			.param("name", "name1")
			.param("author", "author1");
		ResultActions addBookResult = mockMvc.perform(addBookRequest);

		addBookResult
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/book/addBook"))
			.andExpect(flash().attribute("savedBook", hasProperty("isbn", equalTo("isbn1"))))
			.andExpect(flash().attribute("addBookSuccess", true));
	}

	private static Collection<Book> existingBooks() {
		List<Book> books = new ArrayList<>();
		books.add(new Book("isbn1", "name1", "author1"));
		books.add(new Book("isbn2", "name2", "author2"));
		books.add(new Book("isbn3", "name3", "author3"));
		return books;
	}
}