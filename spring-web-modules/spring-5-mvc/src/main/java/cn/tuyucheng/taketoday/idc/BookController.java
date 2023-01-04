package cn.tuyucheng.taketoday.idc;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/book")
public class BookController implements BookOperations {

	private BookRepository repo;

	public BookController(BookRepository repo) {
		this.repo = repo;
	}

	@Override
	public List<Book> getAll() {
		return repo.getItems();
	}

	@Override
	public Optional<Book> getById(int id) {
		return repo.getById(id);
	}

	@Override
	public void save(Book book, int id) {
		repo.save(id, book);
	}

}
