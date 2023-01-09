package cn.tuyucheng.taketoday.servlets;

import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(urlPatterns = "/randomError")
public class RandomErrorServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, final HttpServletResponse resp) {
		throw new IllegalStateException("Random error");
	}
}