package cn.tuyucheng.taketoday.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/randomError")
public class RandomErrorServlet extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest req, final HttpServletResponse resp) {
      throw new IllegalStateException("Random error");
   }
}