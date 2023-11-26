package cn.tuyucheng.taketoday.web;

import cn.tuyucheng.taketoday.persistence.model.Book;
import cn.tuyucheng.taketoday.persistence.repo.BookRepository;
import cn.tuyucheng.taketoday.web.exception.BookIdMismatchException;
import cn.tuyucheng.taketoday.web.exception.BookNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

   @Autowired
   private BookRepository bookRepository;

   @GetMapping
   public Iterable<Book> findAll() {
      return bookRepository.findAll();
   }

   @GetMapping("/title/{bookTitle}")
   public List<Book> findByTitle(@PathVariable String bookTitle) {
      return bookRepository.findByTitle(bookTitle);
   }

   @GetMapping("/{id}")
   public Book findOne(@PathVariable long id) {
      return bookRepository.findById(id)
            .orElseThrow(BookNotFoundException::new);
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Book create(@RequestBody Book book) {
      return bookRepository.save(book);
   }

   @DeleteMapping("/{id}")
   public void delete(@PathVariable long id) {
      bookRepository.findById(id)
            .orElseThrow(BookNotFoundException::new);
      bookRepository.deleteById(id);
   }

   @PutMapping("/{id}")
   public Book updateBook(@RequestBody Book book, @PathVariable long id) {
      if (book.getId() != id) {
         throw new BookIdMismatchException();
      }
      bookRepository.findById(id)
            .orElseThrow(BookNotFoundException::new);
      return bookRepository.save(book);
   }
}