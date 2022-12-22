package cn.tuyucheng.taketoday.boot.jsp.controller;

import cn.tuyucheng.taketoday.boot.jsp.repository.BookRepository;
import cn.tuyucheng.taketoday.boot.jsp.repository.impl.InMemoryBookRepository;
import cn.tuyucheng.taketoday.boot.jsp.repository.model.BookData;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookRepository bookRepository;

	@Test
	@Order(1)
	void whenAddBook_thenBookSaved() throws Exception {
		MockHttpServletRequestBuilder addBookRequest = MockMvcRequestBuilders.post("/book/addBook")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.param("isbn", "isbn1")
			.param("name", "name1")
			.param("author", "author1");
		mockMvc.perform(addBookRequest)
			.andReturn();

		Optional<BookData> storedBookOpt = bookRepository.findById("isbn1");
		assertTrue(storedBookOpt.isPresent());
		assertEquals("name1", storedBookOpt.get().getName());
		assertEquals("author1", storedBookOpt.get().getAuthor());
	}

	@Test
	@Order(2)
	void givenAlreadyExistingBook_whenAddBook_thenShowErrorPage() throws Exception {
		MockHttpServletRequestBuilder addBookRequest = MockMvcRequestBuilders.post("/book/addBook")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.param("isbn", "isbn1")
			.param("name", "name1")
			.param("author", "author1");
		ResultActions addBookResult = mockMvc.perform(addBookRequest);

		addBookResult
			.andExpect(view().name("error-book"))
			.andExpect(model().attribute("ref", "isbn1"))
			.andExpect(model().attribute("object", hasProperty("isbn", equalTo("isbn1"))))
			.andExpect(model().attribute("message", "Cannot add an already existing book"));
	}

	@Configuration
	@ComponentScan("cn.tuyucheng.taketoday.boot.jsp")
	static class ContextConfiguration {

		@Bean
		BookRepository provideBookRepository() {
			return new InMemoryBookRepository(Collections.emptyMap());
		}
	}
}