package cn.tuyucheng.taketoday.patterns.intercepting.filter.commands;

import cn.tuyucheng.taketoday.patterns.intercepting.filter.data.Bookshelf;

import javax.servlet.ServletException;
import java.io.IOException;

public class HomeCommand extends FrontCommand {
	@Override
	public void process() throws ServletException, IOException {
		super.process();
		Bookshelf bookshelf = (Bookshelf) request.getServletContext()
			.getAttribute("bookshelf");
		request.setAttribute("books", bookshelf);
		forward("home");
	}
}
