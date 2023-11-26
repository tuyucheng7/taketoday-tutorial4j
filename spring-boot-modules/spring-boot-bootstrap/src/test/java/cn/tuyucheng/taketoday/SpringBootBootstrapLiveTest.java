package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.persistence.model.Book;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringBootBootstrapLiveTest {

   private static final String API_ROOT = "http://localhost:8080/api/books";

   @Test
   void whenGetAllBooks_thenOK() {
      final Response response = RestAssured.get(API_ROOT);
      assertEquals(HttpStatus.OK.value(), response.getStatusCode());
   }

   @Test
   void whenGetBooksByTitle_thenOK() {
      final Book book = createRandomBook();
      createBookAsUri(book);

      final Response response = RestAssured.get(STR."\{API_ROOT}/title/\{book.getTitle()}");
      assertEquals(HttpStatus.OK.value(), response.getStatusCode());
      assertTrue(response.as(List.class)
            .size() > 0);
   }

   @Test
   void whenGetCreatedBookById_thenOK() {
      final Book book = createRandomBook();
      final String location = createBookAsUri(book);

      final Response response = RestAssured.get(location);
      assertEquals(HttpStatus.OK.value(), response.getStatusCode());
      assertEquals(book.getTitle(), response.jsonPath()
            .get("title"));
   }

   @Test
   void whenGetNotExistBookById_thenNotFound() {
      final Response response = RestAssured.get(STR."\{API_ROOT}/\{randomNumeric(4)}");
      assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
   }

   // POST
   @Test
   void whenCreateNewBook_thenCreated() {
      final Book book = createRandomBook();

      final Response response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(book)
            .post(API_ROOT);
      assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
   }

   @Test
   void whenInvalidBook_thenError() {
      final Book book = createRandomBook();
      book.setAuthor(null);

      final Response response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(book)
            .post(API_ROOT);
      assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
   }

   @Test
   void whenUpdateCreatedBook_thenUpdated() {
      final Book book = createRandomBook();
      final String location = createBookAsUri(book);

      book.setId(Long.parseLong(location.split("api/books/")[1]));
      book.setAuthor("newAuthor");
      Response response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(book)
            .put(location);
      assertEquals(HttpStatus.OK.value(), response.getStatusCode());

      response = RestAssured.get(location);
      assertEquals(HttpStatus.OK.value(), response.getStatusCode());
      assertEquals("newAuthor", response.jsonPath()
            .get("author"));

   }

   @Test
   void whenDeleteCreatedBook_thenOk() {
      final Book book = createRandomBook();
      final String location = createBookAsUri(book);

      Response response = RestAssured.delete(location);
      assertEquals(HttpStatus.OK.value(), response.getStatusCode());

      response = RestAssured.get(location);
      assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
   }

   // ===============================

   private Book createRandomBook() {
      final Book book = new Book();
      book.setTitle(randomAlphabetic(10));
      book.setAuthor(randomAlphabetic(15));
      return book;
   }

   private String createBookAsUri(Book book) {
      final Response response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(book)
            .post(API_ROOT);
      return STR."\{API_ROOT}/\{response.jsonPath().get("id")}";
   }
}