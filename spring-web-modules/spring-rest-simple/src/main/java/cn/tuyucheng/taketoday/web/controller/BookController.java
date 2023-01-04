package cn.tuyucheng.taketoday.web.controller;

import cn.tuyucheng.taketoday.repository.BookRepository;
import cn.tuyucheng.taketoday.web.dto.Book;
import cn.tuyucheng.taketoday.web.error.BookNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookRepository repository;

    @GetMapping("/{id}")
    public Book findById(@PathVariable long id) {
        return repository.findById(id)
              .orElseThrow(() -> new BookNotFoundException(id));
    }
}