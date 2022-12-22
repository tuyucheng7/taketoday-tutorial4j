package cn.tuyucheng.taketoday.boot.jsp.controller;

import cn.tuyucheng.taketoday.boot.jsp.dto.Book;
import cn.tuyucheng.taketoday.boot.jsp.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/book")
public class BookController {

	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping("/viewBooks")
	public String viewBooks(Model model) {
		model.addAttribute("books", bookService.getBooks());
		return "view-books";
	}

	@GetMapping("/addBook")
	public String addBookView(Model model) {
		model.addAttribute("book", new Book());
		return "add-book";
	}

	@PostMapping("/addBook")
	public RedirectView addBook(@ModelAttribute("book") Book book, RedirectAttributes redirectAttributes) {
		final RedirectView redirectView = new RedirectView("/book/addBook", true);
		Book savedBook = bookService.addBook(book);
		redirectAttributes.addFlashAttribute("savedBook", savedBook);
		redirectAttributes.addFlashAttribute("addBookSuccess", true);
		return redirectView;
	}
}