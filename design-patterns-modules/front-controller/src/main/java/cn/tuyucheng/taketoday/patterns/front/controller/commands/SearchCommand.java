package cn.tuyucheng.taketoday.patterns.front.controller.commands;

import cn.tuyucheng.taketoday.patterns.front.controller.data.Book;
import cn.tuyucheng.taketoday.patterns.front.controller.data.BookshelfImpl;

import javax.servlet.ServletException;
import java.io.IOException;

public class SearchCommand extends FrontCommand {
	@Override
	public void process() throws ServletException, IOException {
		Book book = new BookshelfImpl().getInstance()
			.findByTitle(request.getParameter("title"));
		if (book != null) {
			request.setAttribute("book", book);
			forward("book-found");
		} else {
			forward("book-notfound");
		}
	}
}
