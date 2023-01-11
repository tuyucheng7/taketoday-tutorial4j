package cn.tuyucheng.taketoday.boot.json.convertfile.web;

import cn.tuyucheng.taketoday.boot.json.convertfile.ImportUtils;
import cn.tuyucheng.taketoday.boot.json.convertfile.dao.BookRepository;
import cn.tuyucheng.taketoday.boot.json.convertfile.data.Book;
import cn.tuyucheng.taketoday.boot.json.convertfile.service.ImportJsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {
	@Autowired
	private BookRepository books;

	@Autowired
	private ImportJsonService service;

	@PostMapping
	public Book postBook(@RequestBody Book book) throws IOException {
		return books.insert(book);
	}

	@GetMapping
	public List<Book> getBooks() {
		return books.findAll();
	}

	@GetMapping("/{id}")
	public Optional<Book> getBook(@PathVariable String id) {
		return books.findById(id);
	}

	@PostMapping("/import/file")
	public String postJsonFile(@RequestPart("parts") MultipartFile jsonStringsFile) throws IOException {
		List<String> jsonLines = ImportUtils.lines(jsonStringsFile);
		return service.importTo(Book.class, jsonLines);
	}
}