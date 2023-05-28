package cn.tuyucheng.taketoday.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION_TYPE;
import static javax.servlet.RequestDispatcher.ERROR_MESSAGE;
import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

@WebServlet(urlPatterns = "/errorHandler")
public class ErrorHandlerServlet extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws IOException {
      resp.setContentType("text/html; charset=utf-8");
      try (PrintWriter writer = resp.getWriter()) {
         writer.write("<html><head><title>Error description</title></head><body>");
         writer.write("<h2>Error description</h2>");
         writer.write("<ul>");
         Arrays.asList(ERROR_STATUS_CODE, ERROR_EXCEPTION_TYPE, ERROR_MESSAGE)
               .forEach(e ->
                     writer.write("<li>" + e + ":" + req.getAttribute(e) + " </li>")
               );
         writer.write("</ul>");
         writer.write("</html></body>");
      }

      Exception exception = (Exception) req.getAttribute(ERROR_EXCEPTION);
      if (IllegalArgumentException.class.isInstance(exception)) {
         getServletContext().log("Error on an application argument", exception);
      }
   }
}